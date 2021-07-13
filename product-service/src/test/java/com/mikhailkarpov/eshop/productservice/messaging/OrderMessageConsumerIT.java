package com.mikhailkarpov.eshop.productservice.messaging;

import com.mikhailkarpov.eshop.productservice.config.AbstractIT;
import com.mikhailkarpov.eshop.productservice.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderStatus;
import com.mikhailkarpov.eshop.productservice.messaging.message.OrderUpdatedMessage;
import com.mikhailkarpov.eshop.productservice.service.ProductService;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OrderMessageConsumerIT extends AbstractIT {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderMessagingProperties messagingProperties;

    @Autowired
    private ProductService productService;

    private final ParameterizedTypeReference<OrderUpdatedMessage> reference
            = new ParameterizedTypeReference<OrderUpdatedMessage>() {
    };

    @Test
    void givenProductAvailableForReservation_whenOrderCreatedMessageReceived_thenProductReservedAndOrderConfirmedMessageSent() {
        //given
        String code = UUID.randomUUID().toString();
        ProductRequest request = new ProductRequest(code, "title", "desc", 100, 5);
        productService.create(request);

        //when
        UUID orderId = UUID.randomUUID();
        String exchange = messagingProperties.getTopicExchange();
        String routingKey = messagingProperties.getCreatedRoutingKey();
        String messageBody = "{" +
                "\"orderId\": \"" + orderId + "\"," +
                "\"items\": [" +
                "{\"code\": \"" + code + "\", \"quantity\": 4}" +
                "]" +
                "}";
        rabbitTemplate.convertAndSend(exchange, routingKey, MessageBuilder.withBody(messageBody.getBytes()).build());

        //then
        OrderUpdatedMessage message =
                rabbitTemplate.receiveAndConvert(messagingProperties.getUpdatedQueue(), 5000L, reference);

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
        String exchange = messagingProperties.getTopicExchange();
        String routingKey = messagingProperties.getCreatedRoutingKey();
        String messageBody = "{" +
                "\"orderId\": \"" + orderId + "\"," +
                "\"items\": [" +
                "{\"code\": \"" + code1 + "\", \"quantity\": 4}," +
                "{\"code\": \"" + code2 + "\", \"quantity\": 24}" +
                "]" +
                "}";
        rabbitTemplate.convertAndSend(exchange, routingKey, MessageBuilder.withBody(messageBody.getBytes()).build());

        //then
        OrderUpdatedMessage message =
                rabbitTemplate.receiveAndConvert(messagingProperties.getUpdatedQueue(), 5000L, reference);

        Assertions.assertNotNull(message);
        Assertions.assertEquals(orderId, message.getOrderId());
        Assertions.assertEquals(OrderStatus.REJECTED, message.getStatus());
        Assertions.assertEquals(0, productService.findByCode(code1).getReserved());
        Assertions.assertEquals(0, productService.findByCode(code2).getReserved());
    }

    @Test
    void givenNoProduct_whenOrderCreated_thenOrderRejectedMessageSent() {
        //when
        UUID orderId = UUID.randomUUID();
        String exchange = messagingProperties.getTopicExchange();
        String routingKey = messagingProperties.getCreatedRoutingKey();
        String messageBody = "{" +
                "\"orderId\": \"" + orderId + "\"," +
                "\"items\": [" +
                "{\"code\": \"" + UUID.randomUUID() + "\", \"quantity\": 4}" +
                "]" +
                "}";
        rabbitTemplate.convertAndSend(exchange, routingKey, MessageBuilder.withBody(messageBody.getBytes()).build());

        //then
        OrderUpdatedMessage message
                = rabbitTemplate.receiveAndConvert(messagingProperties.getUpdatedQueue(), 5000L, reference);

        Assertions.assertNotNull(message);
        Assertions.assertEquals(orderId, message.getOrderId());
        Assertions.assertEquals(OrderStatus.REJECTED, message.getStatus());
    }
}