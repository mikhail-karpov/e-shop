package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.ProductDTO;

import java.util.List;

public interface ProductQueryService {

    List<ProductDTO> findProductsByOrderId(String orderId);
}
