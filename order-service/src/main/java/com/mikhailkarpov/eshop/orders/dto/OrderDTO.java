package com.mikhailkarpov.eshop.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderDTO {

    private UUID id;

    private String customerId;

    @JsonProperty(value = "shipping-address")
    private AddressDTO shippingAddress;

    @JsonProperty(value = "billing-address")
    private AddressDTO billingAddress;
}
