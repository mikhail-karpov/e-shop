package com.mikhailkarpov.eshop.productservice.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseIT {

    static final PostgreSQLContainer POSTGRES;

    static {
        POSTGRES = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
                .withDatabaseName("product_service")
                .withUsername("postgres")
                .withPassword("password")
                .withReuse(true);

        POSTGRES.start();
    }

    @DynamicPropertySource
    static void configDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void cleanUp() {
        flyway.clean();
        flyway.migrate();
    }
}
