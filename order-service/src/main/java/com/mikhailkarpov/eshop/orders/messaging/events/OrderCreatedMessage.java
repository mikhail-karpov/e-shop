package com.mikhailkarpov.eshop.orders.messaging.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mikhailkarpov.eshop.orders.dto.OrderItemDTO;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class OrderCreatedMessage {

    private final UUID orderId;
    private final List<OrderItemDTO> items;

    public OrderCreatedMessage(@JsonProperty(value = "orderId") UUID orderId,
                               @JsonProperty(value = "items") List<OrderItemDTO> items) {
        this.orderId = orderId;
        this.items = items;
    }
}
