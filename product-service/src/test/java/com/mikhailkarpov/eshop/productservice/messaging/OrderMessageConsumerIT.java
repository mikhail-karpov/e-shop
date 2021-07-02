package com.mikhailkarpov.eshop.productservice.messaging;

import com.mikhailkarpov.eshop.productservice.AbstractIT;
import com.mikhailkarpov.eshop.productservice.AbstractMessagingIT;
import com.mikhailkarpov.eshop.productservice.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderItem;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderStatus;
import com.mikhailkarpov.eshop.productservice.messaging.events.OrderCreatedMessage;
import com.mikhailkarpov.eshop.productservice.messaging.events.OrderUpdatedMessage;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OrderMessageConsumerIT extends AbstractMessagingIT {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderMessagingProperties messagingProperties;

    @Autowired
    ProductRepository productRepository;

    @Test
    void givenProductAvailableForReservation_whenReserveOrder_thenProductReservedAndMessageSent() {
        //given
        String code = UUID.randomUUID().toString();
        Product request = Product.builder()
                .code(code)
                .title("title")
                .description("description")
                .price(100)
                .quantity(5)
                .build();
        productRepository.save(request);

        //when
        UUID orderId = UUID.randomUUID();
        List<OrderItem> items = Collections.singletonList(new OrderItem(code, 4));
        String topicExchange = messagingProperties.getTopicExchange();
        String routingKey = messagingProperties.getCreatedRoutingKey();

        rabbitTemplate.convertAndSend(topicExchange, routingKey, new OrderCreatedMessage(orderId, items));

        //then
        Awaitility.await()
                .atMost(2500L, TimeUnit.MILLISECONDS)
                .until(() -> productRepository.findById(code).get().getReserved(), is(4));
        Awaitility.await()
                .atMost(2500L, TimeUnit.MILLISECONDS)
                .until(orderUpdatedMessageSent(orderId, OrderStatus.CONFIRMED), is(true));
    }

    @Test
    void givenProductNotAvailableForReservation_whenReserveOrder_thenProductNotReservedAndMessageSent() {
        //given
        String code = UUID.randomUUID().toString();
        Product request = Product.builder()
                .code(code)
                .title("title")
                .description("description")
                .price(100)
                .quantity(5)
                .build();
        request.setReserved(2);
        productRepository.save(request);

        //when
        UUID orderId = UUID.randomUUID();
        List<OrderItem> items = Collections.singletonList(new OrderItem(code, 4));
        String topicExchange = messagingProperties.getTopicExchange();
        String routingKey = messagingProperties.getCreatedRoutingKey();

        rabbitTemplate.convertAndSend(topicExchange, routingKey, new OrderCreatedMessage(orderId, items));

        //then
        Awaitility.await()
                .atMost(2500L, TimeUnit.MILLISECONDS)
                .until(() -> productRepository.findById(code).get().getReserved(), is(2));
        Awaitility.await()
                .atMost(2500L, TimeUnit.MILLISECONDS)
                .until(orderUpdatedMessageSent(orderId, OrderStatus.REJECTED), is(true));
    }

    private Callable<Boolean> orderUpdatedMessageSent(UUID orderId, OrderStatus status) {

        String orderUpdatedQueue = messagingProperties.getUpdatedQueue();
        ParameterizedTypeReference<OrderUpdatedMessage> reference =
                new ParameterizedTypeReference<OrderUpdatedMessage>() {
                };
        OrderUpdatedMessage orderUpdatedMessage = rabbitTemplate.receiveAndConvert(orderUpdatedQueue, reference);

        return () -> orderUpdatedMessage != null &&
                orderUpdatedMessage.getOrderId().equals(orderId) &&
                orderUpdatedMessage.getStatus() == status;
    }
}