package com.mikhailkarpov.eshop.productservice.persistence.repository;

import com.mikhailkarpov.eshop.productservice.config.AbstractIT;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductRepositoryCacheIT extends AbstractIT {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    private final String cache = "product";
    private final String code = "macbook";
    private final String notFoundProductCode = UUID.randomUUID().toString();

    @Test
    void givenProduct_whenFindById_thenCached() {
        //when
        Optional<Product> macbook = productRepository.findById(code);
        Product cached = cacheManager.getCache(cache).get(code, Product.class);

        //then
        assertThat(macbook.isPresent()).isTrue();
        assertThat(macbook.get()).isEqualToIgnoringGivenFields(cached, "category");
    }

    @Test
    void givenNoProduct_whenFindById_thenNotCached() {
        //when
        Optional<Product> notFound = productRepository.findById(notFoundProductCode);

        //then
        assertThat(notFound.isPresent()).isFalse();
        assertThat(cacheManager.getCache(cache).get(notFoundProductCode)).isNull();
    }

    @Test
    void givenProductUpdated_whenSave_thenCacheUpdated() {
        //given
        String updatedDescription = "Updated description";

        //when
        Optional<Product> optional = productRepository.findById(code);
        if (optional.isPresent()) {
            Product product = optional.get();
            product.setDescription(updatedDescription);
            productRepository.save(product);
        }

        //then
        String cachedDescription = cacheManager.getCache(cache).get(code, Product.class).getDescription();
        assertThat(cachedDescription).isEqualTo(updatedDescription);
    }

    @Test
    void givenProduct_whenDeleteById_thenRemovedFromCache() {
        //given
        String code = UUID.randomUUID().toString();
        Product product = Product.builder()
                .code(code)
                .title("title")
                .description("Description")
                .price(100)
                .quantity(2)
                .build();
        productRepository.save(product);

        //when
        productRepository.deleteById(code);

        //then
        assertThat(cacheManager.getCache(cache).get(code)).isNull();
    }

    @Test
    @Transactional
    void givenProduct_whenDelete_thenRemovedFromCache() {
        String code = UUID.randomUUID().toString();
        Product product = Product.builder()
                .code(code)
                .title("title")
                .description("Description")
                .price(100)
                .quantity(2)
                .build();
        productRepository.save(product);

        //when
        productRepository.delete(product);

        //then
        assertThat(cacheManager.getCache(cache).get(code)).isNull();
    }
}