package com.mikhailkarpov.eshop.productservice.messaging;

import com.mikhailkarpov.eshop.productservice.exception.OrderReservationException;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderStatus;
import com.mikhailkarpov.eshop.productservice.messaging.message.OrderCreatedMessage;
import com.mikhailkarpov.eshop.productservice.messaging.message.OrderUpdatedMessage;
import com.mikhailkarpov.eshop.productservice.service.OrderReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageConsumer {

    private final OrderReservationService reservationService;
    private final OrderMessagePublisher messagePublisher;

    @RabbitListener(queues = {"${app.messaging.order.createdQueue}"})
    public void onConsume(OrderCreatedMessage message) {

        UUID orderId = message.getOrderId();
        OrderStatus status = OrderStatus.CONFIRMED;

        try {
            reservationService.reserve(message.getItems());
        } catch (OrderReservationException e) {
            status = OrderStatus.REJECTED;
        }

        messagePublisher.send(new OrderUpdatedMessage(orderId, status));
    }
}
