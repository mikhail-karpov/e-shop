package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.service.CategoryService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@WithMockUser(authorities = "SCOPE_category")
class CategoryControllerValidationTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @ParameterizedTest
    @MethodSource("getInvalidArguments")
    void givenInvalidRequest_whenPostCategories_thenBadRequest(String body) throws Exception {
        //when
        mockMvc.perform(post("/categories")
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());

        //then
        verifyNoInteractions(categoryService);
    }

    @ParameterizedTest
    @MethodSource("getInvalidArguments")
    void givenInvalidRequest_whenPutCategory_thenBadRequest(String body) throws Exception {
        //when
        mockMvc.perform(put("/categories/{id}", 2)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());

        //then
        verifyNoInteractions(categoryService);
    }

    @ParameterizedTest
    @MethodSource("getInvalidArguments")
    void givenInvalidRequest_whenPostSubcategory_thenBadRequest(String body) throws Exception {
        //when
        mockMvc.perform(post("/categories/{id}/subcategories", 1)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isBadRequest());

        //then
        verifyNoInteractions(categoryService);
    }

    private static Stream<Arguments> getInvalidArguments() {
        return Stream.of(
                Arguments.of("{\"description\":\"category 1 description\"}"),
                Arguments.of("{\"title\":\"category 1 title\"}"),
                Arguments.of(""),
                Arguments.of("{\"description\":\" \", \"title\": \" \"}")
        );
    }
}