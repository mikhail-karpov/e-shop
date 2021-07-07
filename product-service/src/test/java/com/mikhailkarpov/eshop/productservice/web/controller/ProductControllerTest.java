package com.mikhailkarpov.eshop.productservice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Product abc = new Product("abc", "product 1", "product 1 description", 110, 12);

    private final Product xyz = new Product("xyz", "product 2", "product 2 description", 210, 15);

    // ---------------- GET /products

    @Test
    void givenProducts_whenGetProducts_thenFound() throws Exception {

        when(productService.findAll(any(), any())).thenReturn(new PageImpl(Arrays.asList(abc, xyz)));

        mockMvc.perform(get("/products").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result", hasSize(2)))
                .andExpect(jsonPath("$.result[0].code").value("abc"))
                .andExpect(jsonPath("$.result[0].title").value("product 1"))
                .andExpect(jsonPath("$.result[0].description").value("product 1 description"))
                .andExpect(jsonPath("$.result[0].price").value(110))
                .andExpect(jsonPath("$.result[0].quantity").value(12))
                .andExpect(jsonPath("$.result[1].code").value("xyz"))
                .andExpect(jsonPath("$.result[1].title").value("product 2"))
                .andExpect(jsonPath("$.result[1].description").value("product 2 description"))
                .andExpect(jsonPath("$.result[1].price").value(210))
                .andExpect(jsonPath("$.result[1].quantity").value(15))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(productService).findAll(any(), any());
        verifyNoMoreInteractions(productService);
    }

    @Test
    void givenProducts_whenGetProductsWithTitle_thenFound() throws Exception {

        when(productService.findAll(any(), any())).thenReturn(new PageImpl(Arrays.asList(abc, xyz), PageRequest.of(1, 2), 4));

        mockMvc.perform(get("/products?title=product&category=10&page=1&size=2&sort=code,desc")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result", hasSize(2)))
                .andExpect(jsonPath("$.result[0].code").value("abc"))
                .andExpect(jsonPath("$.result[0].title").value("product 1"))
                .andExpect(jsonPath("$.result[0].description").value("product 1 description"))
                .andExpect(jsonPath("$.result[0].price").value(110))
                .andExpect(jsonPath("$.result[0].quantity").value(12))
                .andExpect(jsonPath("$.result[1].code").value("xyz"))
                .andExpect(jsonPath("$.result[1].title").value("product 2"))
                .andExpect(jsonPath("$.result[1].description").value("product 2 description"))
                .andExpect(jsonPath("$.result[1].price").value(210))
                .andExpect(jsonPath("$.result[1].quantity").value(15))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.totalResults").value(4))
                .andExpect(jsonPath("$.totalPages").value(2));

        verify(productService).findAll(any(), any());
        verifyNoMoreInteractions(productService);
    }

    // ---------------- GET /products/{code}

    @Test
    void givenProductFound_whenGetById_thenFound() throws Exception {

        when(productService.findByCode("abc")).thenReturn(abc);

        mockMvc.perform(get("/products/{code}", "abc")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value("abc"))
                .andExpect(jsonPath("$.title").value("product 1"))
                .andExpect(jsonPath("$.description").value("product 1 description"))
                .andExpect(jsonPath("$.price").value(110))
                .andExpect(jsonPath("$.quantity").value(12));

        verify(productService).findByCode("abc");
        verifyNoMoreInteractions(productService);
    }

    // ---------------- POST /products

    @Test
    void givenValidProductRequest_whenPostProducts_thenCreated() throws Exception {

        when(productService.create(any())).thenReturn(abc);

        String requestBody = "{\"code\":\"abc\"," +
                "\"title\":\"product 1\"," +
                "\"description\":\"product 1 description\"," +
                "\"quantity\":10,\"price\":12}";

        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(requestBody)
                .accept("application/json"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/products/abc"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value("abc"))
                .andExpect(jsonPath("$.title").value("product 1"))
                .andExpect(jsonPath("$.description").value("product 1 description"))
                .andExpect(jsonPath("$.price").value(110))
                .andExpect(jsonPath("$.quantity").value(12));

        verify(productService).create(any());
        verifyNoMoreInteractions(productService);
    }

    // ---------------- PUT /products/{code}

    @Test
    void givenValidProductRequest_whenPutProducts_thenOk() throws Exception {

        when(productService.update(anyString(), any())).thenReturn(abc);

        String requestBody = "{\"code\":\"abc\"," +
                "\"title\":\"product 1\"," +
                "\"description\":\"product 1 description\"," +
                "\"quantity\":10," +
                "\"price\":12}";

        mockMvc.perform(put("/products/{id}", "abc")
                .contentType("application/json")
                .content(requestBody)
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value("abc"))
                .andExpect(jsonPath("$.title").value("product 1"))
                .andExpect(jsonPath("$.description").value("product 1 description"))
                .andExpect(jsonPath("$.price").value(110))
                .andExpect(jsonPath("$.quantity").value(12));

        verify(productService).update(anyString(), any());
        verifyNoMoreInteractions(productService);
    }

    // ---------------- DELETE /products/{code}

    @Test
    void whenDeleteProduct_thenNoContent() throws Exception {

        mockMvc.perform(delete("/products/abc"))
                .andExpect(status().isNoContent());

        verify(productService).delete("abc");
        verifyNoMoreInteractions(productService);
    }
}