package com.mikhailkarpov.eshop.orders.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductReservationResponseMessage {

    private final UUID orderId;
    private final String status;
}
