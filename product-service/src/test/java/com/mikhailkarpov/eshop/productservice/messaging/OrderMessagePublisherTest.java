package com.mikhailkarpov.eshop.productservice.messaging;

import com.mikhailkarpov.eshop.productservice.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderStatus;
import com.mikhailkarpov.eshop.productservice.messaging.message.OrderUpdatedMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class OrderMessagePublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private OrderMessagingProperties messagingProperties;

    @InjectMocks
    private OrderMessagePublisher messagePublisher;

    @Captor
    private ArgumentCaptor<OrderUpdatedMessage> messageCaptor;

    @Test
    void givenMessage_whenSend_thenDelegated() {
        //given
        when(messagingProperties.getTopicExchange()).thenReturn("topic");
        when(messagingProperties.getUpdatedRoutingKey()).thenReturn("key");
        UUID orderId = UUID.randomUUID();
        OrderStatus orderStatus = OrderStatus.REJECTED;

        //when
        messagePublisher.send(new OrderUpdatedMessage(orderId, orderStatus));

        //then
        verify(rabbitTemplate).convertAndSend(eq("topic"), eq("key"), messageCaptor.capture());
        assertEquals(orderId, messageCaptor.getValue().getOrderId());
        assertEquals(orderStatus, messageCaptor.getValue().getStatus());
    }
}