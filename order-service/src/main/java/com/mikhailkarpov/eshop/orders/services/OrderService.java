package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequest;
import com.mikhailkarpov.eshop.orders.dto.OrderDTO;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    void placeOrder(UUID orderId, String customerId, CreateOrderRequest request);

    List<OrderDTO> findAll();

    OrderDTO findOrderById(UUID orderId);
}
