package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.AbstractIntegrationTest;
import com.mikhailkarpov.eshop.productservice.config.RedisCacheConfig;
import com.mikhailkarpov.eshop.productservice.config.RedisProperties;
import com.mikhailkarpov.eshop.productservice.exception.DuplicateProductCodeException;
import com.mikhailkarpov.eshop.productservice.exception.ResourceNotFoundException;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CacheableProductService.class, RedisCacheConfig.class, RedisProperties.class})
class CacheableProductServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private ProductRepository productRepository;

    @BeforeEach
    void invalidateCache() {
        cacheManager.getCache("product").invalidate();
    }

    @Test
    void givenProductRequest_whenCreate_thenCreatedAndCached() {
        //given

        ProductRequest request =
                new ProductRequest("abc", "updated", "updated", 100, 20);
        Product abc = new Product("abc", "title", "description", 1000, 10);

        when(productRepository.existsById("abc")).thenReturn(false);
        when(productRepository.save(any())).thenReturn(abc);

        //when
        Product created = productService.create(request);
        Object cached = cacheManager.getCache("product").get("abc").get();

        //then
        assertThat(abc).usingRecursiveComparison().isEqualTo(created);
        assertThat(cached).usingRecursiveComparison().isEqualTo(created);
    }

    @Test
    void givenProductCodeExists_whenCreate_thenThrowsAndNotCached() {
        //given

        ProductRequest request =
                new ProductRequest("abc", "updated", "updated", 100, 20);
        Product abc = new Product("abc", "title", "description", 1000, 10);

        when(productRepository.existsById("abc")).thenReturn(true);

        //then
        assertThrows(DuplicateProductCodeException.class, () -> productService.create(request));
        assertNull(cacheManager.getCache("product").get("abc"));
    }

    @Test
    void givenProductInCache_whenDelete_thenDeletedAndRemovedFromCache() {
        //given
        Product abc = new Product("abc", "title", "description", 1000, 10);
        when(productRepository.findById("abc")).thenReturn(Optional.of(abc));

        //when
        productService.findByCode("abc");
        assertNotNull(cacheManager.getCache("product").get("abc"));
        productService.delete("abc");

        //then
        assertNull(cacheManager.getCache("product").get("abc"));
    }

    @Test
    void givenProduct_whenFindByCode_thenFoundAndCached() {
        //given
        Product abc = new Product("abc", "title", "description", 1000, 10);
        when(productRepository.findById("abc")).thenReturn(Optional.of(abc));

        //when
        Product found = productService.findByCode("abc");
        Object cached = cacheManager.getCache("product").get("abc").get();

        //then
        assertThat(abc).usingRecursiveComparison().isEqualTo(found);
        assertThat(abc).usingRecursiveComparison().isEqualTo(cached);
    }

    @Test
    void givenNoProduct_whenFindByCode_thenThrowsAndNotCached() {
        //given
        when(productRepository.findById("abc")).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> productService.findByCode("abc"));
        assertNull(cacheManager.getCache("product").get("abc"));
    }

    @Test
    void givenProduct_whenUpdate_thenUpdatedAndCached() {
        //given
        Product abc = new Product("abc", "title", "description", 1000, 10);
        ProductRequest update = new ProductRequest("abc", "updated", "updated", 100, 20);
        when(productRepository.findById("abc")).thenReturn(Optional.of(abc));

        //when
        Product updatedProduct = productService.update("abc", update);
        Object cached = cacheManager.getCache("product").get("abc").get();

        //then
        assertThat(update).usingRecursiveComparison().isEqualTo(updatedProduct);
        assertThat(updatedProduct).usingRecursiveComparison().isEqualTo(cached);
    }

    @Test
    void givenProductNotFound_whenUpdate_thenUpdatedAndCached() {
        //given
        ProductRequest update = new ProductRequest("abc", "updated", "updated", 100, 20);
        when(productRepository.findById("abc")).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> productService.update("abc", update));
        assertNull(cacheManager.getCache("product").get("abc"));
    }
}