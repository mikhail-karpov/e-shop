package com.mikhailkarpov.eshop.orders.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProductReservationRequestMessage {

    private final UUID orderId;
    private final List<OrderItem> items;
}
