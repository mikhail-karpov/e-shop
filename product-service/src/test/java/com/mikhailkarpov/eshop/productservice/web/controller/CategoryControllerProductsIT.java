package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.config.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerProductsIT extends AbstractIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String CATEGORY_SCOPE = "category";

    @Test
    void givenProduct_whenPostProductToCategory_thenOk() {
        //given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/4/products?code=fuji";
        ResponseEntity<Object> response = restTemplate.exchange(url, POST, httpEntity, Object.class);

        //then
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void givenNoProduct_whenPostProductToCategory_thenNotFound() {
        //given
        String code = "not-found";

        //and given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/4/products?code={code}";
        ResponseEntity<Object> response = restTemplate.exchange(url, POST, httpEntity, Object.class, code);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenNoCategory_whenPostProductToCategory_thenNotFound() {
        //given
        String code = "fuji";

        //and given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/555/products?code={code}";
        ResponseEntity<Object> response = restTemplate.exchange(url, POST, httpEntity, Object.class, code);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenProduct_whenDeleteProductFromCategory_thenOk() {
        //given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/3/products?code=macbook";
        ResponseEntity<Object> response = restTemplate.exchange(url, DELETE, httpEntity, Object.class);

        //then
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void givenNoProduct_whenDeleteProductFromCategory_thenNotFound() {
        //given
        //and given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/3/products?code=not-found";
        ResponseEntity<Object> response = restTemplate.exchange(url, DELETE, httpEntity, Object.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenNoCategory_whenDeleteProductFromCategory_thenNotFound() {
        //given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/555/products?code=macbook";
        ResponseEntity<Object> response = restTemplate.exchange(url, DELETE, httpEntity, Object.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }
}