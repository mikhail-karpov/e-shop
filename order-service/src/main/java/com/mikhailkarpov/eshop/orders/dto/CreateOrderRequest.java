package com.mikhailkarpov.eshop.orders.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateOrderRequest {

    @NotEmpty
    private List<@Valid OrderItemDTO> items;

    @NotNull
    @Valid
    private AddressDTO shippingAddress;
}
