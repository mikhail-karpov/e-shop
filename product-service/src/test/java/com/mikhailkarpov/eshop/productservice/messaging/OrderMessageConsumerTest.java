package com.mikhailkarpov.eshop.productservice.messaging;

import com.mikhailkarpov.eshop.productservice.exception.OrderReservationException;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderItem;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderStatus;
import com.mikhailkarpov.eshop.productservice.messaging.events.OrderCreatedMessage;
import com.mikhailkarpov.eshop.productservice.messaging.events.OrderUpdatedMessage;
import com.mikhailkarpov.eshop.productservice.service.OrderReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class OrderMessageConsumerTest {

    @Mock
    private OrderReservationService orderReservationService;

    @Mock
    private OrderMessagePublisher messagePublisher;

    @InjectMocks
    private OrderMessageConsumer messageConsumer;

    @Test
    void givenOrderCreatedMessage_onConsumeMessage_thenOrderReservedAndMessageSent() throws OrderReservationException {
        //given
        UUID orderId = UUID.randomUUID();
        List<OrderItem> items = Arrays.asList(
                new OrderItem("abc", 2),
                new OrderItem("xyz", 3)
        );
        OrderCreatedMessage orderCreatedMessage = new OrderCreatedMessage(orderId, items);

        //when
        messageConsumer.onConsume(orderCreatedMessage);

        //then
        verify(orderReservationService).reserve(items);
        verify(messagePublisher).send(new OrderUpdatedMessage(orderId, OrderStatus.CONFIRMED));
    }

    @Test
    void givenReservationFailed_onConsumeMessage_thenOrderRejectedAndMessageSent() throws OrderReservationException {
        //given
        UUID orderId = UUID.randomUUID();
        List<OrderItem> items = Arrays.asList(
                new OrderItem("abc", 2),
                new OrderItem("xyz", 3)
        );
        OrderCreatedMessage orderCreatedMessage = new OrderCreatedMessage(orderId, items);
        Mockito.doThrow(OrderReservationException.class).when(orderReservationService).reserve(items);

        //when
        messageConsumer.onConsume(orderCreatedMessage);

        //then
        verify(orderReservationService).reserve(items);
        verify(messagePublisher).send(new OrderUpdatedMessage(orderId, OrderStatus.REJECTED));
    }
}