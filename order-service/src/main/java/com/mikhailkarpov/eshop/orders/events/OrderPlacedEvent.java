package com.mikhailkarpov.eshop.orders.events;

import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequest;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderPlacedEvent {

    private final UUID orderId;

    private final String customerId;

    private final CreateOrderRequest request;
}
