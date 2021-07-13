package com.mikhailkarpov.eshop.productservice.messaging;

import com.mikhailkarpov.eshop.productservice.config.AbstractIT;
import com.mikhailkarpov.eshop.productservice.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderStatus;
import com.mikhailkarpov.eshop.productservice.messaging.message.OrderUpdatedMessage;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OrderMessagePublisherIT extends AbstractIT {

    @Autowired
    private OrderMessagePublisher messagePublisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderMessagingProperties messagingProperties;

    @Test
    void test() {
        //given
        UUID orderId = UUID.randomUUID();
        OrderStatus status = OrderStatus.REJECTED;

        //when
        messagePublisher.send(new OrderUpdatedMessage(orderId, status));
        ParameterizedTypeReference<OrderUpdatedMessage> reference = new ParameterizedTypeReference<OrderUpdatedMessage>() {
        };
        OrderUpdatedMessage message = rabbitTemplate.receiveAndConvert(messagingProperties.getUpdatedQueue(), reference);

        //then
        assertNotNull(message);
        assertEquals(orderId, message.getOrderId());
        assertEquals(status, message.getStatus());
    }
}