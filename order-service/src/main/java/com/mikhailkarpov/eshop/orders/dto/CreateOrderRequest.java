package com.mikhailkarpov.eshop.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateOrderRequest {

    @NotEmpty
    private List<@Valid Item> items;

    @NotNull
    @Valid
    @JsonProperty(value = "shipping-address")
    private AddressDTO shippingAddress;

    @NotNull
    @Valid
    @JsonProperty(value = "billing-address")
    private AddressDTO billingAddress;

    @Data
    public static class Item {

        @NotBlank(message = "code is required")
        private String code;

        @NotNull(message = "quantity must be provided")
        @Min(value = 1, message = "quantity must be positive")
        private Integer quantity;
    }
}
