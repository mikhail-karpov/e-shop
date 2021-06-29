package com.mikhailkarpov.eshop.orders.messaging.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import lombok.Value;

import java.util.UUID;

@Value
public class OrderUpdatedMessage {

    private final UUID orderId;
    private final OrderStatus status;

    public OrderUpdatedMessage(@JsonProperty("orderId") UUID orderId,
                               @JsonProperty("status") OrderStatus status) {
        this.orderId = orderId;
        this.status = status;
    }
}
