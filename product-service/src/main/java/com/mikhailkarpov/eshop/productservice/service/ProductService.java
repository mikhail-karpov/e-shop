package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductService {

    Product create(ProductRequest request);

    void delete(String code);

    Page<Product> findAll(Specification<Product> specification, Pageable pageable);

    Product findByCode(String code);

    Product update(String code, ProductRequest update);

}
