package com.mikhailkarpov.eshop.productservice.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class OrderItem {

    private final String code;
    private final Integer quantity;

    public OrderItem(@JsonProperty("code") String code,
                     @JsonProperty("quantity") Integer quantity) {
        this.code = code;
        this.quantity = quantity;
    }
}
