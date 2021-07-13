package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.config.AbstractIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpMethod.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerSecurityIT extends AbstractIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void givenNoAuth_whenHitEndpoints_then401() {
        //when
        ResponseEntity<Object> getProducts =
                restTemplate.getForEntity("/products", Object.class);
        ResponseEntity<Object> getProduct =
                restTemplate.getForEntity("/products/abc", Object.class);
        ResponseEntity<Object> postProduct =
                restTemplate.postForEntity("/products", null, Object.class);
        ResponseEntity<Object> putProduct =
                restTemplate.exchange("/products/abc", PUT, null, Object.class);
        ResponseEntity<Object> deleteProduct =
                restTemplate.exchange("/products/abc", DELETE, null, Object.class);

        //then
        Assertions.assertEquals(401, getProducts.getStatusCodeValue());
        Assertions.assertEquals(401, getProduct.getStatusCodeValue());
        Assertions.assertEquals(401, postProduct.getStatusCodeValue());
        Assertions.assertEquals(401, putProduct.getStatusCodeValue());
        Assertions.assertEquals(401, deleteProduct.getStatusCodeValue());
    }

    @Test
    void givenNoScope_whenHitEndpoints_then403() {
        //given
        String accessToken = obtainAccessToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        ResponseEntity<Object> postProduct =
                restTemplate.exchange("/products", POST, httpEntity, Object.class);
        ResponseEntity<Object> putProduct =
                restTemplate.exchange("/products/abc", PUT, httpEntity, Object.class);
        ResponseEntity<Object> deleteProduct =
                restTemplate.exchange("/products/abc", DELETE, httpEntity, Object.class);

        //then
        Assertions.assertEquals(403, postProduct.getStatusCodeValue());
        Assertions.assertEquals(403, putProduct.getStatusCodeValue());
        Assertions.assertEquals(403, deleteProduct.getStatusCodeValue());
    }
}