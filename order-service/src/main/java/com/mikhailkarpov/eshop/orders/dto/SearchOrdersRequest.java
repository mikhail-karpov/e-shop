package com.mikhailkarpov.eshop.orders.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchOrdersRequest {

    private String customerId;

    private OrderStatus status;
}
