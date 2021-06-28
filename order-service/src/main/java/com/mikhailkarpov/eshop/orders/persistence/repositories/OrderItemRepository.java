package com.mikhailkarpov.eshop.orders.persistence.repositories;

import com.mikhailkarpov.eshop.orders.persistence.entities.OrderItem;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderItemRepository extends CrudRepository<OrderItem, UUID> {

    Iterable<OrderItem> findAllByOrderId(UUID orderId);
}
