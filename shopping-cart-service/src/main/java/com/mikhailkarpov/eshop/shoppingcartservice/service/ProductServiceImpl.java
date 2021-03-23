package com.mikhailkarpov.eshop.shoppingcartservice.service;

import com.mikhailkarpov.eshop.shoppingcartservice.client.ProductServiceClient;
import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductServiceClient productServiceClient;

    @Override
    @Cacheable(value = "product", key = "#code", unless = "#result == null")
    public Optional<Product> getProductByCode(String code) {

        Product product = productServiceClient.getProductByCode(code);
        return Optional.ofNullable(product);
    }
}
