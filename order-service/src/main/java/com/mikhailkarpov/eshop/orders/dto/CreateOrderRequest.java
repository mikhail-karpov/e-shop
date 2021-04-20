package com.mikhailkarpov.eshop.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateOrderRequest {

    @NotEmpty
    private List<@Valid OrderItem> items;

    @NotNull
    @Valid
    @JsonProperty(value = "shipping-address")
    private AddressDTO shippingAddress;
}
