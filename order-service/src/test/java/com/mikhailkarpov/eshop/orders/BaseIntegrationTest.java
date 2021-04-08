package com.mikhailkarpov.eshop.orders;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class BaseIntegrationTest {

    static final PostgreSQLContainer POSTGRES;

    static {
        POSTGRES = (PostgreSQLContainer) new PostgreSQLContainer("postgres:12-alpine")
                .withDatabaseName("order_service")
                .withUsername("order")
                .withPassword("service")
                .withReuse(true);

        POSTGRES.start();
    }

    @DynamicPropertySource
    static void configDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> POSTGRES.getJdbcUrl());
        registry.add("spring.datasource.username", () -> POSTGRES.getUsername());
        registry.add("spring.datasource.password", () -> POSTGRES.getPassword());
    }}
