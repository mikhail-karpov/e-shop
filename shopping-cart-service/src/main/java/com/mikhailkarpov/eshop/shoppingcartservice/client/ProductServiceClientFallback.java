package com.mikhailkarpov.eshop.shoppingcartservice.client;

import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceClientFallback implements ProductServiceClient {

    @Override
    public Product getProductByCode(String code) {
        return null;
    }
}
