package com.mikhailkarpov.eshop.shoppingcartservice.client;

import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        value = "product-service",
        configuration = ProductServiceClientConfig.class,
        fallback = ProductServiceClientFallback.class)
public interface ProductServiceClient {

    @GetMapping("/products/{code}")
    Product getProductByCode(@PathVariable String code);
}
