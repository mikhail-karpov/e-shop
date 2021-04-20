package com.mikhailkarpov.eshop.orders.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OrderItem {

    @NotBlank(message = "code is required")
    private String code;

    @NotNull(message = "quantity must be provided")
    @Min(value = 1, message = "quantity must be positive")
    private Integer quantity;
}
