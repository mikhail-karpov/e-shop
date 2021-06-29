package com.mikhailkarpov.eshop.orders.messaging;

import com.mikhailkarpov.eshop.orders.messaging.events.OrderUpdatedMessage;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import com.mikhailkarpov.eshop.orders.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderMessageConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = {"order-updated-queue"})
    public void onConsume(OrderUpdatedMessage message) {

        UUID orderId = message.getOrderId();
        OrderStatus status = message.getStatus();

        orderService.updateOrderStatus(orderId, status);
    }
}
