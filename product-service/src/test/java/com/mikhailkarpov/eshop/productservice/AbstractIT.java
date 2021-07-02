package com.mikhailkarpov.eshop.productservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class AbstractIT {

    static final PostgreSQLContainer POSTGRES;

    static final GenericContainer REDIS;

    static {
        REDIS = new GenericContainer("redis").withExposedPorts(6379).withReuse(true);

        POSTGRES = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
                .withDatabaseName("product_service")
                .withUsername("postgres")
                .withPassword("password")
                .withReuse(true);

        POSTGRES.start();
        REDIS.start();
    }

    @DynamicPropertySource
    static void configDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @DynamicPropertySource
    private static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS::getHost);
        registry.add("spring.redis.port", REDIS::getFirstMappedPort);
    }
}
