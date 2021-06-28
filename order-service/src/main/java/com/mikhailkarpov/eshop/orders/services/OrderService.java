package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.*;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderItem;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    UUID createOrder(String customerId, CreateOrderRequest request);

    PagedResult<OrderDTO> searchOrders(SearchOrdersRequest request, Pageable pageable);

    PagedResult<OrderDTO> findAll(Pageable pageable);

    OrderWithItemsDTO findOrderById(UUID id);

    void updateOrderStatus(UUID id, OrderStatus status);

}
