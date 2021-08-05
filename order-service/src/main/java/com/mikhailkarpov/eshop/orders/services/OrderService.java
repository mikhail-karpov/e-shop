package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.*;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    UUID createOrder(String customerId, CreateOrderRequestBody request);

    PagedResult<OrderDTO> searchOrders(SearchOrdersRequest request, Pageable pageable);

    PagedResult<OrderDTO> findAll(Pageable pageable);

    OrderDTO findOrderById(UUID id);

    void updateOrderStatus(UUID id, OrderStatus status);

}
