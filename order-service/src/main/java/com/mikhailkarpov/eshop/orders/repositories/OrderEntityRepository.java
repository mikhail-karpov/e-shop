package com.mikhailkarpov.eshop.orders.repositories;

import com.mikhailkarpov.eshop.orders.entities.OrderEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderEntityRepository extends PagingAndSortingRepository<OrderEntity, UUID> {

    @Query("SELECT o " +
            "FROM ORDER o " +
            "LEFT JOIN FETCH o.items " +
            "LEFT JOIN FETCH o.shippingAddress " +
            "LEFT JOIN FETCH o.billingAddress " +
            "WHERE o.id = : id")
    Optional<OrderEntity> findById(@Param("id") UUID id);

    @Query("SELECT o " +
            "FROM ORDER o " +
            "LEFT JOIN FETCH o.items " +
            "LEFT JOIN FETCH o.shippingAddress " +
            "LEFT JOIN FETCH o.billingAddress")
    Iterable<OrderEntity> findAll();
}
