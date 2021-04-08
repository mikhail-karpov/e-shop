package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductDTO> findProductsByOrderId(UUID orderId);
}
