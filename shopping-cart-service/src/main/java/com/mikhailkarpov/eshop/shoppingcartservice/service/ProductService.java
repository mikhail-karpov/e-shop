package com.mikhailkarpov.eshop.shoppingcartservice.service;

import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.Product;

import java.util.Optional;

public interface ProductService {

    Optional<Product> getProductByCode(String code);
}
