package com.mikhailkarpov.eshop.orders.messaging;

import com.mikhailkarpov.eshop.orders.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.orders.dto.OrderItemDTO;
import com.mikhailkarpov.eshop.orders.messaging.events.OrderCreatedMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OrderMessagePublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private OrderMessagingProperties messagingProperties;

    @InjectMocks
    private OrderMessagePublisher messageSender;

    @Captor
    private ArgumentCaptor<OrderCreatedMessage> messageArgumentCaptor;

    @Test
    void givenMessage_whenSendMessage_thenSent() {
        //given
        UUID orderId = UUID.randomUUID();
        OrderItemDTO abcItem = new OrderItemDTO("abc", 1);
        List<OrderItemDTO> items = Collections.singletonList(abcItem);

        when(messagingProperties.getCreatedRoutingKey()).thenReturn("key");
        when(messagingProperties.getTopicExchange()).thenReturn("exchange");

        //when
        messageSender.send(new OrderCreatedMessage(orderId, items));

        //then
        verify(rabbitTemplate).convertAndSend(eq("exchange"), eq("key"), messageArgumentCaptor.capture());

        OrderCreatedMessage message = messageArgumentCaptor.getValue();
        assertThat(message.getOrderId()).isEqualTo(orderId);
        assertThat(message.getItems()).containsAll(items);
    }
}