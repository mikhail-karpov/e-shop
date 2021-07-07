package com.mikhailkarpov.eshop.productservice.messaging;

import com.mikhailkarpov.eshop.productservice.exception.OrderReservationException;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderItem;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderStatus;
import com.mikhailkarpov.eshop.productservice.messaging.message.OrderCreatedMessage;
import com.mikhailkarpov.eshop.productservice.messaging.message.OrderUpdatedMessage;
import com.mikhailkarpov.eshop.productservice.service.OrderReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class OrderMessageConsumerTest {

    @Mock
    private OrderReservationService orderReservationService;

    @Mock
    private OrderMessagePublisher messagePublisher;

    @InjectMocks
    private OrderMessageConsumer messageConsumer;


    @Captor
    private ArgumentCaptor<OrderUpdatedMessage> messageArgumentCaptor;

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
        verify(messagePublisher).send(messageArgumentCaptor.capture());
        assertEquals(orderId, messageArgumentCaptor.getValue().getOrderId());
        assertEquals(OrderStatus.CONFIRMED, messageArgumentCaptor.getValue().getStatus());
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
        doThrow(OrderReservationException.class).when(orderReservationService).reserve(items);

        //when
        messageConsumer.onConsume(orderCreatedMessage);

        //then
        verify(orderReservationService).reserve(items);
        verify(messagePublisher).send(messageArgumentCaptor.capture());
        assertEquals(orderId, messageArgumentCaptor.getValue().getOrderId());
        assertEquals(OrderStatus.REJECTED, messageArgumentCaptor.getValue().getStatus());
    }
}