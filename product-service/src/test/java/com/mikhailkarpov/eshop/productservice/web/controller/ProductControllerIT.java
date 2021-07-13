package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.config.AbstractIT;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.web.dto.PagedResult;
import com.mikhailkarpov.eshop.productservice.web.dto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIT extends AbstractIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String PRODUCT_SCOPE = "product";

    private final ParameterizedTypeReference<PagedResult<Product>> typeRef =
            new ParameterizedTypeReference<PagedResult<Product>>() {
            };

    @Test
    void givenProducts_whenFindProductsByTitle_thenFound() {
        //given
        String accessToken = obtainAccessToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/products?title=macbook";
        ResponseEntity<PagedResult<Product>> response = restTemplate.exchange(url, GET, httpEntity, typeRef);
        PagedResult<Product> pagedResult = response.getBody();

        //then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(pagedResult);
        assertEquals(0, pagedResult.getPage());
        assertEquals(1, pagedResult.getTotalPages());
        assertEquals(1L, pagedResult.getTotalResults());
        assertEquals(1, pagedResult.getResult().size());
    }

    @Test
    void givenProducts_whenFindProductsByCategory_thenFound() {
        //given
        String accessToken = obtainAccessToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/products?category=3";
        ResponseEntity<PagedResult<Product>> response = restTemplate.exchange(url, GET, httpEntity, typeRef);
        PagedResult<Product> pagedResult = response.getBody();

        //then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(pagedResult);
        assertEquals(0, pagedResult.getPage());
        assertEquals(1, pagedResult.getTotalPages());
        assertEquals(2L, pagedResult.getTotalResults());
        assertEquals(2, pagedResult.getResult().size());
    }

    @Test
    void givenProducts_whenFindAll_thenFound() {
        //given
        String accessToken = obtainAccessToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/products?page=1&size=2";
        ResponseEntity<PagedResult<Product>> response = restTemplate.exchange(url, GET, httpEntity, typeRef);
        PagedResult<Product> pagedResult = response.getBody();

        //then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(pagedResult);
        assertEquals(1, pagedResult.getPage());
        assertEquals(2, pagedResult.getTotalPages());
        assertEquals(4L, pagedResult.getTotalResults());
        assertEquals(2, pagedResult.getResult().size());
    }

    @Test
    void givenProduct_whenGetByCode_thenOk() {
        //given
        String accessToken = obtainAccessToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/products/macbook";
        ResponseEntity<Product> response = restTemplate.exchange(url, GET, httpEntity, Product.class);

        //then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void givenNoProduct_whenGetByCode_thenNotFound() {
        //given
        String accessToken = obtainAccessToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/products/not-found";
        ResponseEntity<Product> response = restTemplate.exchange(url, GET, httpEntity, Product.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenValidRequest_whenPostProduct_thenCreated() {
        //given
        String accessToken = obtainAccessToken(PRODUCT_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        ProductRequest request = new ProductRequest("abc", "title", "desc", 100, 5);
        HttpEntity<Object> httpEntity = new HttpEntity<>(request, httpHeaders);

        //when
        String url = "/products";
        ResponseEntity<Product> response = restTemplate.exchange(url, POST, httpEntity, Product.class);
        URI location = response.getHeaders().getLocation();
        Product product = response.getBody();

        //then
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(location);
        assertNotNull(product);
        assertEquals("abc", product.getCode());
        assertEquals("title", product.getTitle());
        assertEquals("desc", product.getDescription());
        assertEquals(100, product.getPrice());
        assertEquals(5, product.getQuantity());

        //and when
        ResponseEntity<Product> getResponse = restTemplate.exchange(location, GET, httpEntity, Product.class);

        //then
        assertEquals(200, getResponse.getStatusCodeValue());
        assertEquals(product, getResponse.getBody());
    }

    @Test
    void givenValidRequest_whenPutProduct_thenUpdated() {
        //given
        String accessToken = obtainAccessToken(PRODUCT_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        ProductRequest request = new ProductRequest("macbook", "title", "desc", 100, 5);
        HttpEntity<Object> httpEntity = new HttpEntity<>(request, httpHeaders);

        //when
        String url = "/products/macbook";
        ResponseEntity<Product> response = restTemplate.exchange(url, PUT, httpEntity, Product.class);
        Product product = response.getBody();

        //then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(product);
        assertEquals("macbook", product.getCode());
        assertEquals("title", product.getTitle());
        assertEquals("desc", product.getDescription());
        assertEquals(100, product.getPrice());
        assertEquals(5, product.getQuantity());

        //and when
        ResponseEntity<Product> getResponse = restTemplate.exchange(url, GET, httpEntity, Product.class);

        //then
        assertEquals(200, getResponse.getStatusCodeValue());
        assertEquals(product, getResponse.getBody());
    }

    @Test
    void givenNoProduct_whenPutProduct_thenNotFound() {
        //given
        String accessToken = obtainAccessToken(PRODUCT_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        ProductRequest request = new ProductRequest("macbook", "title", "desc", 100, 5);
        HttpEntity<Object> httpEntity = new HttpEntity<>(request, httpHeaders);

        //when
        String url = "/products/not-found";
        ResponseEntity<Product> response = restTemplate.exchange(url, PUT, httpEntity, Product.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenProduct_whenDeleteProduct_thenNoContent() {
        //given
        String accessToken = obtainAccessToken(PRODUCT_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/products/macbook";
        ResponseEntity<Object> response = restTemplate.exchange(url, DELETE, httpEntity, Object.class);

        //then
        assertEquals(204, response.getStatusCodeValue());
        assertEquals(404, restTemplate.exchange(url, GET, httpEntity, Product.class).getStatusCodeValue());
    }

    @Test
    void givenNoProduct_whenDeleteProduct_thenNotFound() {
        //given
        String accessToken = obtainAccessToken(PRODUCT_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/products/not-found";
        ResponseEntity<Object> deleteResponse = restTemplate.exchange(url, DELETE, httpEntity, Object.class);

        //then
        assertEquals(404, deleteResponse.getStatusCodeValue());
    }
}