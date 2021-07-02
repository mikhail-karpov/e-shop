package com.mikhailkarpov.eshop.productservice.persistence.repository;

import com.mikhailkarpov.eshop.productservice.AbstractIT;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryIT extends AbstractIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void givenFlywayMigration_whenCountByParentId_thenCount() {

        assertEquals(2, categoryRepository.countByParentId(1L));
    }

    @Test
    void givenFlywayMigration_whenFindByParentId_thenFound() {

        List<Category> subcategories = new ArrayList<>();
        categoryRepository.findAllByParentId(1L).forEach(subcategories::add);

        assertEquals(2, subcategories.size());
    }

    @Test
    void givenCategory_whenSaveAndFindById_thenFound() {
        Category category = new Category();
        category.setTitle("category");

        category = categoryRepository.save(category);
        entityManager.flush();

        assertNotNull(category.getId());
        assertTrue(categoryRepository.findById(category.getId()).isPresent());
    }
}