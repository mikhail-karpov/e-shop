package com.mikhailkarpov.eshop.orders.messaging;

import com.mikhailkarpov.eshop.orders.BaseIT;
import com.mikhailkarpov.eshop.orders.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.orders.dto.OrderItemDTO;
import com.mikhailkarpov.eshop.orders.messaging.events.OrderCreatedMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OrderMessagePublisherIT extends BaseIT {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderMessagePublisher messageSender;

    @Autowired
    private OrderMessagingProperties messagingProperties;

    @Test
    void send() {
        //given
        UUID orderId = UUID.randomUUID();
        OrderItemDTO abcItem = new OrderItemDTO("abc", 2);
        OrderItemDTO xyzItem = new OrderItemDTO("xyz", 5);
        List<OrderItemDTO> items = Arrays.asList(abcItem, xyzItem);
        OrderCreatedMessage message = new OrderCreatedMessage(orderId, items);
        ParameterizedTypeReference<OrderCreatedMessage> reference =
                new ParameterizedTypeReference<OrderCreatedMessage>() {
                };

        //when
        messageSender.send(message);
        OrderCreatedMessage actualMessage =
                rabbitTemplate.receiveAndConvert(messagingProperties.getCreatedQueue(), reference);

        //then
        Assertions.assertThat(actualMessage).isNotNull();
        Assertions.assertThat(actualMessage.getOrderId()).isEqualTo(orderId);
        Assertions.assertThat(actualMessage.getItems()).containsAll(items);
    }
}