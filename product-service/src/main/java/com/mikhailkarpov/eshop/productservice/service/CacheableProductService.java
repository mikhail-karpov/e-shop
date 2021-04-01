package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CacheableProductService extends ProductServiceImpl {

    public CacheableProductService(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    @CacheEvict(cacheNames = {"product"}, key = "#code")
    public void delete(String code) {
        super.delete(code);
    }

    @Override
    @Cacheable(cacheNames = {"product"}, key = "#code", unless = "#result == null")
    public Product findByCode(String code) {
        return super.findByCode(code);
    }

    @Override
    @CachePut(cacheNames = {"product"}, key = "#code", unless = "#result == null")
    public Product update(String code, ProductRequest update) {
        return super.update(code, update);
    }
}
