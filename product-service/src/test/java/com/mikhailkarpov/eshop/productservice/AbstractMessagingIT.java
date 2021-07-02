package com.mikhailkarpov.eshop.productservice;

import com.mikhailkarpov.eshop.productservice.config.OrderMessagingProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public class AbstractMessagingIT extends AbstractIT {

    static final GenericContainer RABBITMQ;

    static {
        RABBITMQ = new GenericContainer("rabbitmq:3-management")
                .withExposedPorts(5672, 15672)
                .withReuse(true);

        RABBITMQ.start();
    }

    @DynamicPropertySource
    static void configMessageBroker(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", RABBITMQ::getContainerIpAddress);
        registry.add("spring.rabbitmq.port", RABBITMQ::getFirstMappedPort);
    }

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private OrderMessagingProperties messagingProperties;

    @AfterEach
    @BeforeEach
    void purgeQueue() {
        rabbitAdmin.purgeQueue(messagingProperties.getUpdatedQueue());
        rabbitAdmin.purgeQueue(messagingProperties.getCreatedQueue());
    }
}
