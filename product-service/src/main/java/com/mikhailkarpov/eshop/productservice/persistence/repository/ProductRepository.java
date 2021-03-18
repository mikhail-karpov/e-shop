package com.mikhailkarpov.eshop.productservice.persistence.repository;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, String> {

}
