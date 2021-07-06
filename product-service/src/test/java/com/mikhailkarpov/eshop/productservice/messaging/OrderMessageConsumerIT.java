package com.mikhailkarpov.eshop.productservice.messaging;

import com.mikhailkarpov.eshop.productservice.AbstractIT;
import com.mikhailkarpov.eshop.productservice.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderItem;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderStatus;
import com.mikhailkarpov.eshop.productservice.messaging.message.OrderCreatedMessage;
import com.mikhailkarpov.eshop.productservice.messaging.message.OrderUpdatedMessage;
import com.mikhailkarpov.eshop.productservice.service.ProductService;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OrderMessageConsumerIT extends AbstractIT {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderMessagingProperties messagingProperties;

    @Autowired
    private ProductService productService;

    @Test
    void givenProductAvailableForReservation_whenOrderCreated_thenProductReservedAndOrderConfirmedMessageSent() {
        //given
        String code = UUID.randomUUID().toString();
        ProductRequest request = new ProductRequest(code, "title", "desc", 100, 5);
        productService.create(request);

        //when
        UUID orderId = UUID.randomUUID();
        List<OrderItem> items = Collections.singletonList(new OrderItem(code, 4));

        rabbitTemplate.convertAndSend(
                messagingProperties.getTopicExchange(),
                messagingProperties.getCreatedRoutingKey(),
                new OrderCreatedMessage(orderId, items)
        );

        //then
        ParameterizedTypeReference<OrderUpdatedMessage> reference = new ParameterizedTypeReference<OrderUpdatedMessage>() {
        };
        OrderUpdatedMessage message = rabbitTemplate.receiveAndConvert(messagingProperties.getUpdatedQueue(), 5000L, reference);

        Assertions.assertNotNull(message);
        Assertions.assertEquals(orderId, message.getOrderId());
        Assertions.assertEquals(OrderStatus.CONFIRMED, message.getStatus());
        Assertions.assertEquals(4, productService.findByCode(code).getReserved());
    }

    @Test
    void givenNotEnoughProduct_whenOrderCreated_thenProductNotReservedAndOrderRejectedMessageSent() {
        //given
        String code1 = UUID.randomUUID().toString();
        ProductRequest request = new ProductRequest(code1, "title", "desc", 100, 5);
        productService.create(request);

        String code2 = UUID.randomUUID().toString();
        request = new ProductRequest(code2, "title 2", "desc", 100, 15);
        productService.create(request);

        //when
        UUID orderId = UUID.randomUUID();
        List<OrderItem> items = Arrays.asList(new OrderItem(code1, 4), new OrderItem(code2, 24));

        rabbitTemplate.convertAndSend(
                messagingProperties.getTopicExchange(),
                messagingProperties.getCreatedRoutingKey(),
                new OrderCreatedMessage(orderId, items)
        );

        //then
        ParameterizedTypeReference<OrderUpdatedMessage> reference = new ParameterizedTypeReference<OrderUpdatedMessage>() {
        };
        OrderUpdatedMessage message = rabbitTemplate.receiveAndConvert(messagingProperties.getUpdatedQueue(), 5000L, reference);

        Assertions.assertNotNull(message);
        Assertions.assertEquals(orderId, message.getOrderId());
        Assertions.assertEquals(OrderStatus.REJECTED, message.getStatus());
        Assertions.assertEquals(0, productService.findByCode(code1).getReserved());
        Assertions.assertEquals(0, productService.findByCode(code2).getReserved());
    }

    @Test
    void givenNoProduct_whenOrderCreated_thenOrderRejectedMessageSent() {
        //given
        //when
        UUID orderId = UUID.randomUUID();
        List<OrderItem> items = Collections.singletonList(new OrderItem(UUID.randomUUID().toString(), 4));

        rabbitTemplate.convertAndSend(
                messagingProperties.getTopicExchange(),
                messagingProperties.getCreatedRoutingKey(),
                new OrderCreatedMessage(orderId, items)
        );

        //then
        ParameterizedTypeReference<OrderUpdatedMessage> reference = new ParameterizedTypeReference<OrderUpdatedMessage>() {
        };
        OrderUpdatedMessage message = rabbitTemplate.receiveAndConvert(messagingProperties.getUpdatedQueue(), 5000L, reference);

        Assertions.assertNotNull(message);
        Assertions.assertEquals(orderId, message.getOrderId());
        Assertions.assertEquals(OrderStatus.REJECTED, message.getStatus());
    }
}