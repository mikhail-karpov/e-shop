package com.mikhailkarpov.eshop.orders.repositories;

import com.mikhailkarpov.eshop.orders.entities.OrderEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderEntityRepository extends PagingAndSortingRepository<OrderEntity, UUID> {

}
