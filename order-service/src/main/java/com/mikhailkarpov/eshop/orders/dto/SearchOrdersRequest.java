package com.mikhailkarpov.eshop.orders.dto;

import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchOrdersRequest {

    private String customerId;

    private OrderStatus status;
}
