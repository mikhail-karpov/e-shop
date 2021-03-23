package com.mikhailkarpov.eshop.shoppingcartservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "app.service.shopping-cart.cookie")
@Getter
@Setter
public class CookieConfig {

    @NotNull
    @Min(1)
    private Integer ageHours;

}
