package com.mikhailkarpov.eshop.orders.dto;

import com.mikhailkarpov.eshop.orders.persistence.entities.OrderItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class OrderItemDTO {

    @NotBlank(message = "code must be provided")
    private String code;

    @NotNull(message = "quantity must be provided")
    @Min(value = 1L, message = "quantity must be positive")
    private Integer quantity;

    public OrderItemDTO(String code, Integer quantity) {
        this.code = code;
        this.quantity = quantity;
    }

    public OrderItemDTO(OrderItem item) {
        this.code = item.getCode();
        this.quantity = item.getQuantity();
    }
}
