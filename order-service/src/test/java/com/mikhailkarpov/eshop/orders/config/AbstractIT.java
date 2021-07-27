package com.mikhailkarpov.eshop.orders.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractIT {

    static final PostgreSQLContainer POSTGRES;
    static final GenericContainer RABBITMQ;
    static final KeycloakContainer KEYCLOAK;

    static {
        POSTGRES = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
                .withDatabaseName("order_service")
                .withUsername("order_service")
                .withPassword("password")
                .withReuse(true);

        RABBITMQ = new GenericContainer("rabbitmq:3-management")
                .withExposedPorts(5672, 15672)
                .withReuse(true);

        KEYCLOAK = new KeycloakContainer("jboss/keycloak")
                .withRealmImportFile("/order-realm.json")
                .withReuse(true);

        POSTGRES.start();
        RABBITMQ.start();
        KEYCLOAK.start();
    }

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

    @DynamicPropertySource
    private static void configureKeycloak(DynamicPropertyRegistry registry) {
        registry.add("app.security.oauth2.jwt.issuer-uri", AbstractIT::jwtIssuerUri);
    }

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

    private static String jwtIssuerUri() {
        return String.format("%s/realms/order", KEYCLOAK.getAuthServerUrl());
    }

    protected String obtainUserAccessToken() {
        return obtainAccessToken("user", "user-pass");
    }

    protected String obtainAdminAccessToken() {
        return obtainAccessToken("admin", "admin-pass");
    }

    private String obtainAccessToken(String username, String password) {

        String clientId = "order-service";
        String clientSecret = "67715df9-d9f4-42b5-bc81-f58868ef0149";

        Map<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("grant_type", "password");
        params.put("username", username);
        params.put("password", password);

        //@formatter:off
        return RestAssured
                .given()
                    .contentType("application/x-www-form-urlencoded")
                .   formParams(params)
                .when()
                    .post(jwtIssuerUri() + "/protocol/openid-connect/token")
                .then()
                    .assertThat().statusCode(200)
                .extract()
                    .path("access_token");
        //@formatter:on
    }
}
