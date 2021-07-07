package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.exception.OrderReservationException;
import com.mikhailkarpov.eshop.productservice.exception.ProductReservationException;
import com.mikhailkarpov.eshop.productservice.exception.ResourceNotFoundException;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderItem;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReservationServiceImpl implements OrderReservationService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void reserve(List<OrderItem> items) throws OrderReservationException {

        List<Product> products = new ArrayList<>();

        for (OrderItem item : items) {
            String code = item.getCode();

            Product product = productRepository.findById(code).orElseThrow(() -> {
                String message = String.format("Product (code=%s) not found", code);
                return new OrderReservationException(new ResourceNotFoundException(message));
            });

            try {
                int quantity = item.getQuantity();
                product.addReserved(quantity);
                products.add(product);

            } catch (ProductReservationException e) {
                String message = String.format("Product (code=%s) reservation failed", code);
                throw new OrderReservationException(message, e);
            }
        }

        products.forEach(product -> productRepository.save(product));
    }
}
