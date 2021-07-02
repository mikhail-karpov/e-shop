package com.mikhailkarpov.eshop.productservice.messaging;

import com.mikhailkarpov.eshop.productservice.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.productservice.messaging.message.OrderUpdatedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final OrderMessagingProperties messagingProperties;

    public void send(OrderUpdatedMessage message) {

        String exchange = messagingProperties.getTopicExchange();
        String routingKey = messagingProperties.getUpdatedRoutingKey();

        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
