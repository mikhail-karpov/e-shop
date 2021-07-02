package com.mikhailkarpov.eshop.productservice.persistence.repository;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<Product, String>,
        JpaSpecificationExecutor<Product> {

    @Override
    @Cacheable(cacheNames = {"product"}, key = "#code", unless = "#result == null")
    Optional<Product> findById(String code);

    @Override
    @CachePut(cacheNames = {"product"}, key = "#root.args[0].code")
    Product save(Product product);

    @Override
    @CacheEvict(cacheNames = {"product"}, key = "#code")
    void deleteById(String code);

    @Override
    @CacheEvict(cacheNames = {"product"}, key = "#root.args[0].code")
    void delete(Product product);

    long countByCategoryId(Long categoryId);
}
