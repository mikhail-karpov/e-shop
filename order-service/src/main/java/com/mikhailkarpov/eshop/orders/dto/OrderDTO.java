package com.mikhailkarpov.eshop.orders.dto;

import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderDTO {

    private UUID id;
    private String customerId;
    private OrderStatus status;
    private AddressDTO shippingAddress;

    @Builder
    public OrderDTO(UUID id, String customerId, OrderStatus status, AddressDTO shippingAddress) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.shippingAddress = shippingAddress;
    }
}
