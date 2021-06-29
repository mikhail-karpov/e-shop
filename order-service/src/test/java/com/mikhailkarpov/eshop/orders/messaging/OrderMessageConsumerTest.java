package com.mikhailkarpov.eshop.orders.messaging;

import com.mikhailkarpov.eshop.orders.messaging.events.OrderUpdatedMessage;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import com.mikhailkarpov.eshop.orders.services.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class OrderMessageConsumerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderMessageConsumer messageConsumer;

    @Test
    void givenMessage_whenConsumeMessage_thenStatusUpdated() {
        //given
        UUID orderId = UUID.randomUUID();
        OrderStatus status = OrderStatus.CONFIRMED;
        OrderUpdatedMessage message = new OrderUpdatedMessage(orderId, status);

        //when
        messageConsumer.onConsume(message);

        //then
        Mockito.verify(orderService).updateOrderStatus(orderId, status);
    }

}