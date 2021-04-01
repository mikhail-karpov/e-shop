package com.mikhailkarpov.eshop.shoppingcartservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public abstract class AbstractIntegrationTest {

    static final GenericContainer REDIS;

    static {
        REDIS = new GenericContainer("redis:alpine").withExposedPorts(6379);

        REDIS.start();
    }

    @DynamicPropertySource
    static void setUpRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", () -> REDIS.getHost());
        registry.add("spring.redis.port", () -> REDIS.getFirstMappedPort());
    }
}
