package com.mikhailkarpov.eshop.productservice;

import com.mikhailkarpov.eshop.productservice.config.OrderMessagingProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public class AbstractIT extends DatabaseIT {

    static final GenericContainer REDIS;
    static final GenericContainer RABBITMQ;

    static {
        REDIS = new GenericContainer("redis").withExposedPorts(6379).withReuse(true);

        RABBITMQ = new GenericContainer("rabbitmq:3-management")
                .withExposedPorts(5672, 15672)
                .withReuse(true);

        REDIS.start();
        RABBITMQ.start();
    }

    @DynamicPropertySource
    static void configMessageBroker(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", RABBITMQ::getContainerIpAddress);
        registry.add("spring.rabbitmq.port", RABBITMQ::getFirstMappedPort);
    }

    @DynamicPropertySource
    private static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS::getHost);
        registry.add("spring.redis.port", REDIS::getFirstMappedPort);
    }

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private OrderMessagingProperties messagingProperties;

    @AfterEach
    @BeforeEach
    void purgeQueues() {
        rabbitAdmin.purgeQueue(messagingProperties.getUpdatedQueue());
        rabbitAdmin.purgeQueue(messagingProperties.getCreatedQueue());
    }

    @BeforeEach
    void invalidateCache() {
        cacheManager.getCacheNames().forEach(cache -> cacheManager.getCache(cache).invalidate());
    }
}
