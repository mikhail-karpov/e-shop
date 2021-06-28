package com.mikhailkarpov.eshop.orders.persistence.repositories;

import com.mikhailkarpov.eshop.orders.persistence.entities.Order;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends PagingAndSortingRepository<Order, UUID>,
        JpaSpecificationExecutor<Order> {

    @Override
    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findById(@Param("id") UUID uuid);
}
