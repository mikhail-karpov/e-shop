package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.config.AbstractIT;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Category;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryRequest;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class CategoryControllerIT extends AbstractIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String CATEGORY_SCOPE = "category";

    @Test
    void givenCategories_whenGetCategories_thenParentCategoriesReturned() {
        //given
        String accessToken = obtainAccessToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories";
        ResponseEntity<CategoryResponse[]> response =
                restTemplate.exchange(url, GET, httpEntity, CategoryResponse[].class);

        //then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }

    @Test
    void givenCategory_whenGetCategoryById_thenCategoryReturned() {
        //given
        String accessToken = obtainAccessToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/3";
        ResponseEntity<CategoryResponse> response =
                restTemplate.exchange(url, GET, httpEntity, CategoryResponse.class);
        CategoryResponse category = response.getBody();

        //then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(category);
        assertEquals(3L, category.getId());
        assertEquals("Computers", category.getTitle());
        assertEquals("Computers and peripherals", category.getDescription());
    }

    @Test
    void givenNoCategory_whenGetCategoryById_thenNotFound() {
        String accessToken = obtainAccessToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/999";
        ResponseEntity<CategoryResponse> response =
                restTemplate.exchange(url, GET, httpEntity, CategoryResponse.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenCategoryRequestAndCategoryScope_whenPostCategories_thenCategoryCreatedAndFound() {
        //given
        CategoryRequest request = new CategoryRequest();
        request.setTitle("new category");
        request.setDescription("new category description");

        //and given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<CategoryRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

        //when
        String url = "/categories";
        ResponseEntity<CategoryResponse> postResponse =
                restTemplate.exchange(url, POST, httpEntity, CategoryResponse.class);
        CategoryResponse category = postResponse.getBody();

        //then
        assertEquals(201, postResponse.getStatusCodeValue());
        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals("new category", category.getTitle());
        assertEquals("new category description", category.getDescription());

        //and when
        URI location = postResponse.getHeaders().getLocation();
        ResponseEntity<Category> getResponse = restTemplate.exchange(location, GET, httpEntity, Category.class);

        //then
        assertEquals(200, getResponse.getStatusCodeValue());
    }

    @Test
    void givenCategoryRequest_whenPutCategories_thenCategoryUpdated() {
        //given
        CategoryRequest request = new CategoryRequest();
        request.setTitle("updated category");
        request.setDescription("updated category description");

        //and given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<CategoryRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

        //when
        String url = "/categories/3";
        ResponseEntity<CategoryResponse> response =
                restTemplate.exchange(url, PUT, httpEntity, CategoryResponse.class);
        CategoryResponse category = response.getBody();

        //then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(category);
        assertEquals(3L, category.getId());
        assertEquals("updated category", category.getTitle());
        assertEquals("updated category description", category.getDescription());
    }

    @Test
    void givenNoCategory_whenPutCategories_thenNotFound() {
        //given
        CategoryRequest request = new CategoryRequest();
        request.setTitle("updated category");
        request.setDescription("updated category description");

        //and given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<CategoryRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

        //when
        String url = "/categories/999";
        ResponseEntity<Object> response = restTemplate.exchange(url, PUT, httpEntity, Object.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenNotEmptyCategory_whenDeleteCategory_thenNotDeleted() {
        //and given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String url = "/categories/3";
        ResponseEntity<Object> deleteResponse =
                restTemplate.exchange(url, DELETE, httpEntity, Object.class);
        ResponseEntity<Object> getResponse =
                restTemplate.exchange(url, GET, httpEntity, Object.class);

        //then
        assertEquals(400, deleteResponse.getStatusCodeValue());
        assertEquals(200, getResponse.getStatusCodeValue());
    }

    @Test
    void givenNotEmptyCategory_whenDeleteCategoryForced_thenDeleted() {
        //given
        String accessToken = obtainAccessToken(CATEGORY_SCOPE);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        //when
        String deleteCategoryUrl = "/categories/3?forced=true";
        ResponseEntity<Object> deleteResponse =
                restTemplate.exchange(deleteCategoryUrl, DELETE, httpEntity, Object.class);

        //and when
        String getCategoryUrl = "/categories/3";
        ResponseEntity<CategoryResponse> getResponse =
                restTemplate.exchange(getCategoryUrl, GET, httpEntity, CategoryResponse.class);

        //then
        assertEquals(204, deleteResponse.getStatusCodeValue());
        assertEquals(404, getResponse.getStatusCodeValue());
    }
}