package com.mikhailkarpov.eshop.productservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Configuration
@ConfigurationProperties
public class RedisProperties {

    @Value("${spring.redis.host}")
    @NotNull
    private String host;

    @Value("${spring.redis.port}")
    @NotNull
    private Integer port;
}
