package com.mikhailkarpov.eshop.productservice;

import com.mikhailkarpov.eshop.productservice.config.OrderMessagingProperties;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

import java.util.HashMap;
import java.util.Map;

public class AbstractIT extends DatabaseIT {

    static final GenericContainer REDIS;
    static final GenericContainer RABBITMQ;
    static final KeycloakContainer KEYCLOAK;

    static {
        REDIS = new GenericContainer("redis")
                .withExposedPorts(6379)
                .withReuse(true);

        RABBITMQ = new GenericContainer("rabbitmq:3-management")
                .withExposedPorts(5672, 15672)
                .withReuse(true);

        KEYCLOAK = new KeycloakContainer()
                .withRealmImportFile("/product_realm.json")
                .withReuse(true);

        REDIS.start();
        RABBITMQ.start();
        KEYCLOAK.start();
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

    @DynamicPropertySource
    private static void configureKeycloak(DynamicPropertyRegistry registry) {
        registry.add("app.security.oauth2.jwt.issuer-uri", AbstractIT::jwtIssuerUri);
    }

    private static String jwtIssuerUri() {
        return String.format("%s/realms/product", KEYCLOAK.getAuthServerUrl());
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


    protected String obtainAccessToken() {
        return obtainAccessToken(null);
    }

    protected String obtainAccessToken(String scope) {

        String clientId = "product-service";
        String clientSecret = "d6b910e5-972d-4191-9b2c-57329d89864c";
        String authUrl = jwtIssuerUri();

        Map<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("grant_type", "password");
        params.put("username", "user");
        params.put("password", "user-pass");

        if (scope != null) {
            params.put("scope", scope);
        }

        //@formatter:off
        return RestAssured
                .given()
                    .contentType("application/x-www-form-urlencoded")
                    .formParams(params)
                .when()
                    .post(authUrl + "/protocol/openid-connect/token")
                .then()
                    .assertThat().statusCode(200)
                    .extract().path("access_token");
        //@formatter:on
    }
}
