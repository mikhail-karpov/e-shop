package com.mikhailkarpov.eshop.productservice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhailkarpov.eshop.productservice.service.ProductService;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@WithMockUser(authorities = "SCOPE_product")
class ProductControllerValidationTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @NullSource
    @MethodSource("getInvalidProducts")
    void givenInvalidRequest_whenPostProducts_thenBadRequest(ProductRequest request) throws Exception {

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("getInvalidProducts")
    void givenInvalidProductRequest_whenPutProducts_thenBadRequest(ProductRequest request) throws Exception {

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/products/abc")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    private static Stream<Arguments> getInvalidProducts() {
        return Stream.of(
                of(new ProductRequest(null, "product 1", "description 1", 1100, 15)),
                of(new ProductRequest("abc", null, "description 1", 1100, 15)),
                of(new ProductRequest("abc", "product 1", null, 1100, 15)),
                of(new ProductRequest("abc", "product 1", "description 1", null, 15)),
                of(new ProductRequest("abc", "product 1", "description 1", 1100, null)),
                of(new ProductRequest("abc", "product 1", "description 1", 0, 15)),
                of(new ProductRequest("abc", "product 1", "description 1", 1100, -1))
        );
    }
}