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
class CategoryControllerSecurityIT extends AbstractIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void givenNoAuth_whenHitEndpoints_then401() {
        //when
        ResponseEntity<Object> getCategories =
                restTemplate.getForEntity("/categories", Object.class);
        ResponseEntity<Object> getCategory =
                restTemplate.getForEntity("/categories/1", Object.class);
        ResponseEntity<Object> postCategory =
                restTemplate.postForEntity("/categories", null, Object.class);
        ResponseEntity<Object> putCategory =
                restTemplate.exchange("/categories/1", PUT, null, Object.class);
        ResponseEntity<Object> deleteCategory =
                restTemplate.exchange("/categories/1", DELETE, null, Object.class);
        ResponseEntity<Object> getSubcategories =
                restTemplate.getForEntity("/categories/1subcategories", Object.class);
        ResponseEntity<Object> postSubcategory =
                restTemplate.postForEntity("/categories/1subcategories", null, Object.class);
        ResponseEntity<Object> postProduct =
                restTemplate.postForEntity("/categories/1/products?code=macbook", null, Object.class);
        ResponseEntity<Object> deleteProduct =
                restTemplate.exchange("/categories/1/products?code=macbook", DELETE, null, Object.class);

        //then
        Assertions.assertEquals(401, getCategories.getStatusCodeValue());
        Assertions.assertEquals(401, getCategory.getStatusCodeValue());
        Assertions.assertEquals(401, postCategory.getStatusCodeValue());
        Assertions.assertEquals(401, putCategory.getStatusCodeValue());
        Assertions.assertEquals(401, deleteCategory.getStatusCodeValue());
        Assertions.assertEquals(401, getSubcategories.getStatusCodeValue());
        Assertions.assertEquals(401, postSubcategory.getStatusCodeValue());
        Assertions.assertEquals(401, postProduct.getStatusCodeValue());
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
        ResponseEntity<Object> postCategory =
                restTemplate.exchange("/categories", POST, httpEntity, Object.class);
        ResponseEntity<Object> putCategory =
                restTemplate.exchange("/categories/1", PUT, httpEntity, Object.class);
        ResponseEntity<Object> deleteCategory =
                restTemplate.exchange("/categories/1", DELETE, httpEntity, Object.class);
        ResponseEntity<Object> postSubcategory =
                restTemplate.exchange("/categories/1subcategories", POST, httpEntity, Object.class);
        ResponseEntity<Object> postProduct =
                restTemplate.exchange("/categories/1/products?code=macbook", POST, httpEntity, Object.class);
        ResponseEntity<Object> deleteProduct =
                restTemplate.exchange("/categories/1/products?code=macbook", DELETE, httpEntity, Object.class);

        //then
        Assertions.assertEquals(403, postCategory.getStatusCodeValue());
        Assertions.assertEquals(403, putCategory.getStatusCodeValue());
        Assertions.assertEquals(403, deleteCategory.getStatusCodeValue());
        Assertions.assertEquals(403, postSubcategory.getStatusCodeValue());
        Assertions.assertEquals(403, postProduct.getStatusCodeValue());
        Assertions.assertEquals(403, deleteProduct.getStatusCodeValue());
    }
}