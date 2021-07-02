package com.mikhailkarpov.eshop.productservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Configuration
@ConfigurationProperties(prefix = "app.messaging.order")
@Getter
@Setter
public class OrderMessagingProperties {

    @NotBlank
    private String topicExchange;

    @NotBlank
    private String createdQueue;

    @NotBlank
    private String updatedQueue;

    @NotBlank
    private String createdRoutingKey;

    @NotBlank
    private String updatedRoutingKey;
}
