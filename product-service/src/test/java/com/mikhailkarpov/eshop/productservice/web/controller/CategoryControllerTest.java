package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.persistence.entity.Category;
import com.mikhailkarpov.eshop.productservice.service.CategoryService;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void whenGetCategories_thenReturnParentCategoriesSortedByTitle() throws Exception {

        Category category1 = new Category("category 1", "category 1 description");
        category1.setId(1L);

        Category category2 = new Category("category 2", "category 2 description");
        category2.setId(2L);

        when(categoryService.findParentCategories()).thenReturn(Arrays.asList(category1, category2));

        mockMvc.perform(get("/categories")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].title").value("category 1"))
                .andExpect(jsonPath("[0].description").value("category 1 description"))
                .andExpect(jsonPath("[1].id").value(2))
                .andExpect(jsonPath("[1].title").value("category 2"))
                .andExpect(jsonPath("[1].description").value("category 2 description"));

        verify(categoryService).findParentCategories();
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void whenGetCategoryById_thenReturnCategory() throws Exception {

        Category category1 = new Category("category 1", "category 1 description");
        category1.setId(1L);

        when(categoryService.findById(1L)).thenReturn(category1);

        mockMvc.perform(get("/categories/{id}", 1)
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("category 1"))
                .andExpect(jsonPath("$.description").value("category 1 description"));

        verify(categoryService).findById(1L);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void givenValidCategoryRequest_whenPostCategories_thenCreated() throws Exception {

        Category expected = new Category("category 1", "category 1 description");
        expected.setId(1L);

        when(categoryService.create(any(CategoryRequest.class))).thenReturn(expected);

        mockMvc.perform(post("/categories")
                .contentType("application/json")
                .content("{\"title\":\"category 1\", \"description\":\"category 1 description\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/categories/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("category 1"))
                .andExpect(jsonPath("$.description").value("category 1 description"));

        verify(categoryService).create(any(CategoryRequest.class));
        verifyNoMoreInteractions(categoryService);
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"{\"description\":\"category 1 description\"}"})
    void givenNoTitle_whenPostCategories_thenBadRequest(String body) throws Exception {

        mockMvc.perform(post("/categories")
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    @Test
    void givenValidCategoryRequest_whenPutCategory_thenUpdated() throws Exception {

        Category expected = new Category("category 1", "category 1 description");
        expected.setId(1L);

        when(categoryService.update(anyLong(), any(CategoryRequest.class))).thenReturn(expected);

        mockMvc.perform(put("/categories/{id}", 1)
                .contentType("application/json")
                .content("{\"title\":\"category 1\", \"description\":\"category 1 description\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("category 1"))
                .andExpect(jsonPath("$.description").value("category 1 description"));

        verify(categoryService).update(anyLong(), any(CategoryRequest.class));
        verifyNoMoreInteractions(categoryService);
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"{\"description\":\"category 1 description\"}"})
    void givenNoTitle_whenPutCategory_thenBadRequest(String body) throws Exception {

        mockMvc.perform(put("/categories/{id}", 2)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    @Test
    void givenCategoryExists_whenDeleteCategory_thenNoContent() throws Exception {

        mockMvc.perform(delete("/categories/{id}", 2))
                .andExpect(status().isNoContent());

        verify(categoryService).delete(2L, false);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void givenCategoryExistsAnd_whenForcedDeleteCategory_thenNoContent() throws Exception {

        mockMvc.perform(delete("/categories/{id}?forced={forced}", 2, true))
                .andExpect(status().isNoContent());

        verify(categoryService).delete(2L, true);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void whenGetSubcategories_thenFoundAndSortedByTitle() throws Exception {

        Category category1 = new Category("category 1", "category 1 description");
        category1.setId(1L);

        Category category2 = new Category("category 2", "category 2 description");
        category2.setId(2L);

        when(categoryService.findSubcategoriesByParentId(1L)).thenReturn(Arrays.asList(category1, category2));

        mockMvc.perform(get("/categories/{id}/subcategories", 1)
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].title").value("category 1"))
                .andExpect(jsonPath("[0].description").value("category 1 description"))
                .andExpect(jsonPath("[1].id").value(2))
                .andExpect(jsonPath("[1].title").value("category 2"))
                .andExpect(jsonPath("[1].description").value("category 2 description"));

        verify(categoryService).findSubcategoriesByParentId(1L);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void givenValidCategoryRequest_whenPostSubcategories_thenCreated() throws Exception {

        Category expected = new Category("category 1", "category 1 description");
        expected.setId(3L);

        when(categoryService.createSubcategory(anyLong(), any(CategoryRequest.class))).thenReturn(expected);

        mockMvc.perform(post("/categories/{id}/subcategories", 1)
                .contentType("application/json")
                .content("{\"title\":\"category 1\", \"description\":\"category 1 description\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/categories/3"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("category 1"))
                .andExpect(jsonPath("$.description").value("category 1 description"));

        verify(categoryService).createSubcategory(anyLong(), any(CategoryRequest.class));
        verifyNoMoreInteractions(categoryService);
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"{\"description\":\"category 1 description\"}"})
    void givenNoTitle_whenPostSubcategory_thenBadRequest(String body) throws Exception {

        mockMvc.perform(post("/categories/{id}/subcategories", 1)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    @Test
    void whenAddProduct_thenOk() throws Exception {

        mockMvc.perform(post("/categories/{id}/products?code={code}", 1, "abc"))
                .andExpect(status().isOk());

        verify(categoryService).addProduct(1L, "abc");
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void whenDeleteProduct_thenOk() throws Exception {

        mockMvc.perform(delete("/categories/{id}/products?code={code}", 1, "abc"))
                .andExpect(status().isOk());

        verify(categoryService).removeProduct(1L, "abc");
        verifyNoMoreInteractions(categoryService);
    }
}