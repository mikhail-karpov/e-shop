package com.mikhailkarpov.eshop.productservice;

import org.junit.jupiter.api.AfterAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class AbstractIntegrationTest {

    static final PostgreSQLContainer postgres;
    static final GenericContainer config;
    static final GenericContainer eureka;

    static {
        postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:12-alpine")
                .withDatabaseName("product_service")
                .withUsername("postgres")
                .withPassword("password")
                .withReuse(true);

        config = new GenericContainer("springcloud/configserver")
                .withReuse(true);

        eureka = new GenericContainer("springcloud/eureka")
                .withReuse(true);

        postgres.start();
        config.start();
        eureka.start();
    }

    @DynamicPropertySource
    static void configDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
