package com.mikhailkarpov.eshop.orders.repositories;

import com.mikhailkarpov.eshop.orders.entities.OrderItemEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OrderItemEntityRepository extends PagingAndSortingRepository<OrderItemEntity, String> {

    @Query("SELECT i " +
            "FROM OrderItem i " +
            "WHERE i.order.id = :id")
    Iterable<OrderItemEntity> findAllByOrderId(@Param("id") UUID id);
}
