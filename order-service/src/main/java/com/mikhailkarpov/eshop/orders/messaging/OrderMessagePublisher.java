package com.mikhailkarpov.eshop.orders.messaging;

import com.mikhailkarpov.eshop.orders.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.orders.messaging.events.OrderCreatedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final OrderMessagingProperties messagingProperties;

    public void send(OrderCreatedMessage message) {

        String orderTopicExchange = messagingProperties.getTopicExchange();
        String orderCreatedRouterKey = messagingProperties.getCreatedRoutingKey();

        rabbitTemplate.convertAndSend(orderTopicExchange, orderCreatedRouterKey, message);
    }
}
