package com.mikhailkarpov.eshop.orders.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateOrderRequest {

    @NotEmpty
    private List<OrderItemDTO> items;

    @NotNull
    private AddressDTO shippingAddress;
}
