package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.config.AbstractIT;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryRequest;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerSubcategoryIT extends AbstractIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String CATEGORY_SCOPE = "category";

    @Test
    void givenSubcategories_whenGetSubcategories_thenOk() {
        //and given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/1/subcategories";
        ResponseEntity<CategoryResponse[]> response =
                restTemplate.exchange(url, GET, httpEntity, CategoryResponse[].class);
        CategoryResponse[] subcategories = response.getBody();

        //then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(subcategories);
        assertEquals(2, subcategories.length);
    }

    @Test
    void givenNoSubcategories_whenGetSubcategories_thenNotFound() {
        //given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/111/subcategories";
        ResponseEntity<Object> response = restTemplate.exchange(url, GET, httpEntity, Object.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenCategory_whenPostSubcategory_thenCreated() {
        //given
        CategoryRequest request = new CategoryRequest("title", "description");

        //and given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<CategoryRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

        //when
        String url = "/categories/2/subcategories";
        ResponseEntity<CategoryResponse> response =
                restTemplate.exchange(url, POST, httpEntity, CategoryResponse.class);
        URI location = response.getHeaders().getLocation();
        CategoryResponse subcategory = response.getBody();

        //then
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(location);
        assertNotNull(subcategory);
        assertNotNull(subcategory.getId());
        assertEquals("title", subcategory.getTitle());
        assertEquals("description", subcategory.getDescription());

        //and then
        assertEquals(subcategory,
                restTemplate.exchange(location, GET, httpEntity, CategoryResponse.class).getBody());
    }

    @Test
    void givenNoCategory_whenPostSubcategory_thenNotFound() {
        //given
        CategoryRequest request = new CategoryRequest("title", "description");

        //and given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<CategoryRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

        //when
        String url = "/categories/222/subcategories";
        ResponseEntity<CategoryResponse> response =
                restTemplate.exchange(url, POST, httpEntity, CategoryResponse.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenProduct_whenPostProductToCategory_thenOk() {
        //given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/4/products?code=fuji";
        ResponseEntity<Object> response =
                restTemplate.exchange(url, POST, httpEntity, Object.class);

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
        ResponseEntity<Object> response =
                restTemplate.exchange(url, POST, httpEntity, Object.class, code);

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