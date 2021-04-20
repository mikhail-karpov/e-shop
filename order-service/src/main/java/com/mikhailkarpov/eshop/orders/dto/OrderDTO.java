package com.mikhailkarpov.eshop.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mikhailkarpov.eshop.orders.entities.OrderStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderDTO {

    private UUID id;

    @JsonProperty(value = "customer-id")
    private String customerId;

    @JsonProperty(value = "shipping-address")
    private AddressDTO shippingAddress;

    private OrderStatusDTO status;
}
