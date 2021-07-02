package com.mikhailkarpov.eshop.productservice.messaging;

import com.mikhailkarpov.eshop.productservice.AbstractMessagingIT;
import com.mikhailkarpov.eshop.productservice.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderStatus;
import com.mikhailkarpov.eshop.productservice.messaging.message.OrderUpdatedMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OrderMessagePublisherIT extends AbstractMessagingIT {

    @Autowired
    private OrderMessagePublisher messagePublisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderMessagingProperties messagingProperties;

    @Test
    void givenOrderUpdatedMessage_whenSendMessage_thenMessageSent() {
        //given
        UUID orderId = UUID.randomUUID();
        OrderStatus status = OrderStatus.REJECTED;

        //when
        messagePublisher.send(new OrderUpdatedMessage(orderId, status));
        ParameterizedTypeReference<OrderUpdatedMessage> reference = new ParameterizedTypeReference<OrderUpdatedMessage>() {
        };
        OrderUpdatedMessage orderUpdatedMessage =
                rabbitTemplate.receiveAndConvert(messagingProperties.getUpdatedQueue(), reference);

        //then
        Assertions.assertThat(orderUpdatedMessage.getOrderId()).isEqualTo(orderId);
        Assertions.assertThat(orderUpdatedMessage.getStatus()).isEqualTo(status);
    }
}