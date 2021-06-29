package com.mikhailkarpov.eshop.orders;

import com.mikhailkarpov.eshop.orders.persistence.repositories.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class BaseIT {

    static final PostgreSQLContainer POSTGRES;
    static final GenericContainer RABBITMQ;

    static {
        POSTGRES = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
                .withDatabaseName("order_service")
                .withUsername("order_service")
                .withPassword("password")
                .withReuse(true);

        RABBITMQ = new GenericContainer("rabbitmq:3-management")
                .withExposedPorts(5672, 15672)
                .withReuse(true);

        POSTGRES.start();
        RABBITMQ.start();
    }

    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource
    static void configDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @DynamicPropertySource
    static void configMessageBroker(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", RABBITMQ::getContainerIpAddress);
        registry.add("spring.rabbitmq.port", RABBITMQ::getFirstMappedPort);
    }

    @AfterEach
    protected void cleanUp() {
        orderRepository.deleteAll();
    }
}
