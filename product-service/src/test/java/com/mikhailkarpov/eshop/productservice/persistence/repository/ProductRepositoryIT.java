package com.mikhailkarpov.eshop.productservice.persistence.repository;

import com.mikhailkarpov.eshop.productservice.config.DatabaseIT;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Category;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryIT extends DatabaseIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void givenFlywayMigration_whenCountByCategoryId_thenCount() {

        assertEquals(2L, productRepository.countByCategoryId(3L));
    }

    @Test
    void givenProduct_whenSaveAndFindByCode_thenSavedAndFound() {
        //given
        Category category = entityManager.find(Category.class, 3L);

        Product abc = Product.builder()
                .code("abc")
                .title("title")
                .description("abc description")
                .price(100)
                .quantity(10)
                .build();

        //when
        productRepository.save(abc);
        entityManager.flush();

        //then
        Optional<Product> found = productRepository.findById("abc");
        assertTrue(found.isPresent());
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(abc);
    }
}