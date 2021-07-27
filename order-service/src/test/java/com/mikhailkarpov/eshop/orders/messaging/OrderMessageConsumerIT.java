package com.mikhailkarpov.eshop.orders.messaging;

import com.mikhailkarpov.eshop.orders.config.AbstractIT;
import com.mikhailkarpov.eshop.orders.config.OrderMessagingProperties;
import com.mikhailkarpov.eshop.orders.persistence.entities.Order;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderItem;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import com.mikhailkarpov.eshop.orders.persistence.repositories.OrderRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus.ACCEPTED;
import static com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus.REJECTED;
import static com.mikhailkarpov.eshop.orders.utils.PojoUtils.getValidAddress;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OrderMessageConsumerIT extends AbstractIT {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderMessagingProperties messagingProperties;

    @Autowired
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        OrderItem abcItem = new OrderItem("abc", 2);
        OrderItem xyzItem = new OrderItem("xyz", 3);

        Set<OrderItem> items = new HashSet<>();
        items.add(abcItem);
        items.add(xyzItem);

        order = orderRepository.save(new Order("customerId", getValidAddress(), ACCEPTED, items));
    }

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
    }

    @Test
    void givenMessage_whenConsumed_thenOrderStatusUpdated() {
        //given
        String orderExchange = messagingProperties.getTopicExchange();
        String key = messagingProperties.getUpdatedRoutingKey();
        String body = "{\"status\": \"REJECTED\", \"orderId\": \"" + order.getId().toString() + "\"}";

        //when
        rabbitTemplate.convertAndSend(orderExchange, key, MessageBuilder.withBody(body.getBytes()).build());

        //then
        Awaitility.await()
                .atMost(2500L, MILLISECONDS)
                .until(() -> getOrderStatus(order.getId()), is(REJECTED));
    }

    private OrderStatus getOrderStatus(UUID orderId) {
        Optional<Order> foundOrder = orderRepository.findById(orderId);
        return foundOrder.isPresent() ? foundOrder.get().getStatus() : null;
    }

}