package com.mikhailkarpov.eshop.orders.events;

import com.mikhailkarpov.eshop.orders.entities.AddressEntity;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class OrderCreatedEvent {

    private UUID orderId;

    private String customerId;

    private AddressEntity shippingAddress;

    private AddressEntity billingAddress;

    private Map<String, Integer> productsCodeToQuantity;
}
