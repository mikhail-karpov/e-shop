package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequest;

public interface OrderCommandService {

    String placeOrder(CreateOrderRequest request);
}
