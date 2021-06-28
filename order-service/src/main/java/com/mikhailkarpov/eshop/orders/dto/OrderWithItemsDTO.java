package com.mikhailkarpov.eshop.orders.dto;

import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderWithItemsDTO extends OrderDTO {

    private List<OrderItemDTO> items;

    public OrderWithItemsDTO(UUID id, String customerId, OrderStatus status, AddressDTO shippingAddress, List<OrderItemDTO> items) {
        super(id, customerId, status, shippingAddress);
        this.items = items;
    }
}
