package com.mikhailkarpov.eshop.shoppingcartservice.client;

import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ProductServiceClientFallbackTest {

    private final ProductServiceClientFallback fallback = new ProductServiceClientFallback();

    @Test
    void whenGetByCode_thenReturnNull() {
        //when
        Product product = fallback.getProductByCode("abc");

        //then
        assertNull(product);
    }
}