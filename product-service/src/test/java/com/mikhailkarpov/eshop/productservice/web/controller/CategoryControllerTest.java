package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.service.CategoryService;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryRequest;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
@WithMockUser
class CategoryControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void whenGetCategories_thenReturnParentCategoriesSortedByTitle() throws Exception {
        //given
        CategoryResponse category1 = new CategoryResponse(1L, "category 2", "category 2 description");
        CategoryResponse category2 = new CategoryResponse(2L, "category 1", "category 1 description");
        when(categoryService.findParentCategories()).thenReturn(Arrays.asList(category1, category2));

        //when
        mockMvc.perform(get("/categories")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0].id").value(2))
                .andExpect(jsonPath("[0].title").value("category 1"))
                .andExpect(jsonPath("[0].description").value("category 1 description"))
                .andExpect(jsonPath("[1].id").value(1))
                .andExpect(jsonPath("[1].title").value("category 2"))
                .andExpect(jsonPath("[1].description").value("category 2 description"));

        //then
        verify(categoryService).findParentCategories();
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void whenGetCategoryById_thenReturnCategory() throws Exception {
        //given
        CategoryResponse category1 = new CategoryResponse(1L, "category 1", "category 1 description");
        when(categoryService.findById(1L)).thenReturn(category1);

        //when
        mockMvc.perform(get("/categories/{id}", 1)
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("category 1"))
                .andExpect(jsonPath("$.description").value("category 1 description"));

        //then
        verify(categoryService).findById(1L);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_category")
    void givenValidCategoryRequest_whenPostCategories_thenCreated() throws Exception {
        //given
        CategoryResponse expected = new CategoryResponse(1L, "category 1", "category 1 description");
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(expected);

        //when
        mockMvc.perform(post("/categories")
                .contentType("application/json")
                .content("{\"title\":\"category 1\", \"description\":\"category 1 description\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/categories/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("category 1"))
                .andExpect(jsonPath("$.description").value("category 1 description"));

        //then
        verify(categoryService).createCategory(any(CategoryRequest.class));
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_category")
    void givenValidCategoryRequest_whenPutCategory_thenUpdated() throws Exception {
        //given
        CategoryResponse expected = new CategoryResponse(1L, "category 1", "category 1 description");
        when(categoryService.update(anyLong(), any(CategoryRequest.class))).thenReturn(expected);

        //when
        mockMvc.perform(put("/categories/{id}", 1)
                .contentType("application/json")
                .content("{\"title\":\"category 1\", \"description\":\"category 1 description\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("category 1"))
                .andExpect(jsonPath("$.description").value("category 1 description"));

        //then
        verify(categoryService).update(anyLong(), any(CategoryRequest.class));
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_category")
    void givenCategoryExists_whenDeleteCategory_thenNoContent() throws Exception {
        //when
        mockMvc.perform(delete("/categories/{id}", 2))
                .andExpect(status().isNoContent());

        //then
        verify(categoryService).delete(2L, false);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_category")
    void givenCategoryExistsAnd_whenForcedDeleteCategory_thenNoContent() throws Exception {
        //when
        mockMvc.perform(delete("/categories/{id}?forced={forced}", 2, true))
                .andExpect(status().isNoContent());

        //then
        verify(categoryService).delete(2L, true);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void whenGetSubcategories_thenFoundAndSortedByTitle() throws Exception {
        //given
        CategoryResponse category1 = new CategoryResponse(1L, "category 1", "category 1 description");
        CategoryResponse category2 = new CategoryResponse(2L, "category 2", "category 2 description");
        when(categoryService.findSubcategoriesByParentId(1L)).thenReturn(Arrays.asList(category2, category1));

        //when
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

        //then
        verify(categoryService).findSubcategoriesByParentId(1L);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_category")
    void givenValidCategoryRequest_whenPostSubcategories_thenCreated() throws Exception {
        //given
        CategoryResponse expected = new CategoryResponse(3L, "category 1", "category 1 description");
        when(categoryService.createSubcategory(anyLong(), any(CategoryRequest.class))).thenReturn(expected);

        //when
        mockMvc.perform(post("/categories/{id}/subcategories", 1)
                .contentType("application/json")
                .content("{\"title\":\"category 1\", \"description\":\"category 1 description\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/categories/3"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("category 1"))
                .andExpect(jsonPath("$.description").value("category 1 description"));

        //then
        verify(categoryService).createSubcategory(anyLong(), any(CategoryRequest.class));
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_category")
    void whenAddProduct_thenOk() throws Exception {
        //when
        mockMvc.perform(post("/categories/{id}/products?code={code}", 1, "abc"))
                .andExpect(status().isOk());

        //then
        verify(categoryService).addProduct(1L, "abc");
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_category")
    void whenDeleteProduct_thenOk() throws Exception {
        //when
        mockMvc.perform(delete("/categories/{id}/products?code={code}", 1, "abc"))
                .andExpect(status().isOk());

        //then
        verify(categoryService).removeProduct(1L, "abc");
        verifyNoMoreInteractions(categoryService);
    }
}