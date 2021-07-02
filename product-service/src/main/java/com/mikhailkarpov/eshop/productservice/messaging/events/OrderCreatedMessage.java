package com.mikhailkarpov.eshop.productservice.messaging.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderItem;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class OrderCreatedMessage {

    private final UUID orderId;
    private final List<OrderItem> items;

    public OrderCreatedMessage(@JsonProperty(value = "orderId") UUID orderId,
                               @JsonProperty(value = "items") List<OrderItem> items) {
        this.orderId = orderId;
        this.items = items;
    }
}
