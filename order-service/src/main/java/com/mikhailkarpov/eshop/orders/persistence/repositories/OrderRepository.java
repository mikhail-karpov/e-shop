package com.mikhailkarpov.eshop.orders.persistence.repositories;

import com.mikhailkarpov.eshop.orders.persistence.entities.Order;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface OrderRepository extends PagingAndSortingRepository<Order, UUID>,
        JpaSpecificationExecutor<Order> {

}
