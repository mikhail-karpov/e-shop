package com.mikhailkarpov.eshop.productservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public class AbstractIntegrationTest {

    static final PostgreSQLContainer postgres;

    static {
        postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:12-alpine")
                .withDatabaseName("product_service")
                .withUsername("postgres")
                .withPassword("password")
                .withReuse(true);

        postgres.start();
    }

    @DynamicPropertySource
    static void configDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
