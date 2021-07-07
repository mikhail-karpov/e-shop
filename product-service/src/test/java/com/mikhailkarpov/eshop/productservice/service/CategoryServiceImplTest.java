package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Category;
import com.mikhailkarpov.eshop.productservice.persistence.repository.CategoryRepository;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryRequest;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository, productRepository);
    }

    @Test
    void givenCategoryRequest_whenCreate_thenReturned() {
        //given
        CategoryRequest request = new CategoryRequest();
        request.setTitle("category 1");
        request.setDescription("category 1 description");

        Category expectedCategory = new Category("category 1", "category 1 description");
        expectedCategory.setId(2L);

        when(categoryRepository.save(any(Category.class))).thenReturn(expectedCategory);

        //when
        CategoryResponse createdCategory = categoryService.createCategory(request);

        //then
        assertEquals(expectedCategory.getId(), createdCategory.getId());
        assertEquals(expectedCategory.getTitle(), createdCategory.getTitle());
        assertEquals(expectedCategory.getDescription(), createdCategory.getDescription());

        verify(categoryRepository).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void givenCategoryRequest_whenCreateSubcategory_thenReturned() {
        //given
        CategoryRequest request = new CategoryRequest();
        request.setTitle("category 1");
        request.setDescription("category 1 description");

        Category parentCategory = new Category("parent", "parent category description");
        parentCategory.setId(1L);

        Category expectedCategory = parentCategory.createSubcategory("subcategory", "subcategory description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(expectedCategory);

        //when
        CategoryResponse createdCategory = categoryService.createSubcategory(1L, request);

        //then
        assertEquals(expectedCategory.getId(), createdCategory.getId());
        assertEquals(expectedCategory.getTitle(), createdCategory.getTitle());
        assertEquals(expectedCategory.getDescription(), createdCategory.getDescription());

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepository);
    }
}