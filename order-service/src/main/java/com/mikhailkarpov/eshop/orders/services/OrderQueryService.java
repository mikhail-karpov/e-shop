package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.entities.OrderEntity;

import java.util.List;

public interface OrderQueryService {

    List<OrderEntity> findAll();

    OrderEntity findOrderById(String orderId);
}
