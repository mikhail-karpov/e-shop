package com.mikhailkarpov.eshop.productservice.persistence.specification;

import com.mikhailkarpov.eshop.productservice.AbstractIT;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductSpecificationIT extends AbstractIT {

    @Autowired
    ProductRepository productRepository;

    @Test
    void givenFlyWay_whenFindWithTitleLike_thenFound() {

        List<Product> products = productRepository.findAll(ProductSpecification.titleLike("%dell%"));
        assertEquals(1, products.size());

        List<Product> notFound = productRepository.findAll(ProductSpecification.titleLike("samsung"));
        assertTrue(notFound.isEmpty());
    }

    @Test
    void givenFlyWay_whenFindWithCategoryId_thenFound() {

        List<Product> products = productRepository.findAll(ProductSpecification.categoryIdEqual(3L));
        assertEquals(2, products.size());
    }


}