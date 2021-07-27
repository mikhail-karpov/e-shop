package com.mikhailkarpov.eshop.orders.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mikhailkarpov.eshop.orders.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequestBody;
import com.mikhailkarpov.eshop.orders.messaging.events.OrderCreatedMessage;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import static org.assertj.core.api.Assertions.assertThat;

class OrderControllerMessagingIT extends OrderControllerIT {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderMessagingProperties messagingProperties;

    @Test
    void whenCreateOrderAndGetOrderById_thenOrderAccepted() throws JsonProcessingException {
        //given
        CreateOrderRequestBody createOrderRequestBody = createOrderRequestBody();
        String accessToken = obtainUserAccessToken();

        //when
        //@formatter:off
        RestAssured
            .given()
                .auth().oauth2(accessToken)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(createOrderRequestBody))
            .when()
                .post("http://localhost:" + port + "/orders")
            .then()
                .statusCode(201);
        //@formatter:on

        //and given
        ParameterizedTypeReference<OrderCreatedMessage> reference =
                new ParameterizedTypeReference<OrderCreatedMessage>() {
                };

        //when
        OrderCreatedMessage message =
                rabbitTemplate.receiveAndConvert(messagingProperties.getCreatedQueue(), reference);

        //then
        assertThat(message).isNotNull();
        assertThat(message.getItems()).isNotEmpty();
        assertThat(message.getItems()).containsAll(createOrderRequestBody.getItems());
    }
}