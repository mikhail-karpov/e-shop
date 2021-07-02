package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.exception.OrderReservationException;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderItem;

import java.util.List;

public interface OrderReservationService {

    void reserve(List<OrderItem> items) throws OrderReservationException;
}
