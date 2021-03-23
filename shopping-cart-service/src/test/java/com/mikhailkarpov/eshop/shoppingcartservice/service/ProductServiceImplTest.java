package com.mikhailkarpov.eshop.shoppingcartservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mikhailkarpov.eshop.shoppingcartservice.client.ProductServiceClient;
import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductServiceClient productServiceClient;

    private ProductService productService;

    @BeforeEach
    void setup() {
        this.productService = new ProductServiceImpl(productServiceClient);
    }

    @Test
    void givenProduct_whenGetProductByCode_thenPresent() throws JsonProcessingException {
        //given
        Product abc = new Product("abc", "product abc", 100, 10);
        when(productServiceClient.getProductByCode("abc")).thenReturn(abc);

        //when
        Optional<Product> product = productService.getProductByCode("abc");

        //then
        assertTrue(product.isPresent());
        assertThat(abc).usingRecursiveComparison().isEqualTo(product.get());

        verify(productServiceClient).getProductByCode("abc");
        verifyNoMoreInteractions(productServiceClient);
    }

    @Test
    void givenNull_whenGetProductByCode_thenEmpty() throws JsonProcessingException {

        //when
        Optional<Product> product = productService.getProductByCode("not-found");

        //then
        assertFalse(product.isPresent());

        verify(productServiceClient).getProductByCode("not-found");
        verifyNoMoreInteractions(productServiceClient);
    }


}
