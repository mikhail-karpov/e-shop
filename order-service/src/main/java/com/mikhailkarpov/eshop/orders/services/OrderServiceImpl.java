package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.entities.OrderEntity;
import com.mikhailkarpov.eshop.orders.exceptions.OrderNotFoundException;
import com.mikhailkarpov.eshop.orders.repositories.OrderEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderQueryService {

    private final OrderEntityRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> findAll() {

        List<OrderEntity> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderEntity findOrderById(String orderId) {

        try {
            return orderRepository.findById(UUID.fromString(orderId)).get();

        } catch (IllegalArgumentException | NoSuchElementException e) {
            String message = String.format("Order with id=\"%s\" not found", orderId);
            throw new OrderNotFoundException(message);
        }
    }
}
