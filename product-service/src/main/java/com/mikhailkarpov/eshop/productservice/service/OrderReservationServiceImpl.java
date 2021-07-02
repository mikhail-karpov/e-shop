package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.exception.OrderReservationException;
import com.mikhailkarpov.eshop.productservice.exception.ResourceNotFoundException;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderItem;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReservationServiceImpl implements OrderReservationService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void reserve(List<OrderItem> items) throws OrderReservationException {

        for (OrderItem item : items) {
            String code = item.getCode();
            Product product = productRepository.findById(code).orElseThrow(() -> {
                String message = String.format("Product with code=%s not found", code);
                return new OrderReservationException(new ResourceNotFoundException(message));
            });

            int reserved = product.getReserved();
            int quantity = product.getQuantity();
            int tobeReserved = item.getQuantity();

            if (tobeReserved > quantity - reserved) {
                String message = String.format("Not enough product with code=%s for reservation", code);
                throw new OrderReservationException(message);
            }
            product.setReserved(reserved + tobeReserved);
            productRepository.save(product);
        }
    }
}
