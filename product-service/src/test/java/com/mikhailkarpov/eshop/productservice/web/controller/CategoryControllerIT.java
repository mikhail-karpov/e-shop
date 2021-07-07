package com.mikhailkarpov.eshop.productservice.web.controller;

import com.mikhailkarpov.eshop.productservice.AbstractIT;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Category;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryRequest;
import com.mikhailkarpov.eshop.productservice.web.dto.CategoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerIT extends AbstractIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void givenCategories_whenGetCategories_thenParentCategoriesReturned() {
        //when
        String url = "/categories";
        ResponseEntity<CategoryResponse[]> response = restTemplate.getForEntity(url, CategoryResponse[].class);

        //then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }

    @Test
    void givenCategory_whenGetCategoryById_thenCategoryReturned() {
        //when
        String url = "/categories/3";
        ResponseEntity<CategoryResponse> response = restTemplate.getForEntity(url, CategoryResponse.class);
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
        //when
        String url = "/categories/999";
        ResponseEntity<CategoryResponse> response = restTemplate.getForEntity(url, CategoryResponse.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenCategoryRequest_whenPostCategories_thenCategoryCreatedAndFound() {
        //given
        CategoryRequest request = new CategoryRequest();
        request.setTitle("new category");
        request.setDescription("new category description");

        //when
        String url = "/categories";
        ResponseEntity<CategoryResponse> response = restTemplate.postForEntity(url, request, CategoryResponse.class);
        CategoryResponse category = response.getBody();

        //then
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals("new category", category.getTitle());
        assertEquals("new category description", category.getDescription());
        assertEquals(200,
                restTemplate.getForEntity(response.getHeaders().getLocation(), Category.class).getStatusCodeValue());
    }

    @ParameterizedTest
    @MethodSource("getInvalidCategoryRequest")
    void givenInvalidCategoryRequest_whenPostCategories_thenBadRequest(CategoryRequest request) {
        //when
        String url = "/categories";
        ResponseEntity<CategoryResponse> response = restTemplate.postForEntity(url, request, CategoryResponse.class);

        //then
        assertEquals(400, response.getStatusCodeValue());
    }

    static Stream<Arguments> getInvalidCategoryRequest() {
        return Stream.of(
                Arguments.of(new CategoryRequest(null, "description")),
                Arguments.of(new CategoryRequest("", "description")),
                Arguments.of(new CategoryRequest("title", null)),
                Arguments.of(new CategoryRequest("title", ""))
        );
    }

    @Test
    void givenCategoryRequest_whenPutCategories_thenCategoryUpdated() {
        //given
        CategoryRequest request = new CategoryRequest();
        request.setTitle("updated category");
        request.setDescription("updated category description");
        HttpEntity<CategoryRequest> requestEntity = new HttpEntity<>(request);

        //when
        String url = "/categories/3";
        ResponseEntity<CategoryResponse> response = restTemplate.exchange(url, PUT, requestEntity, CategoryResponse.class);
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
        HttpEntity<CategoryRequest> requestEntity = new HttpEntity<>(request);

        //when
        String url = "/categories/999";
        ResponseEntity<Object> response = restTemplate.exchange(url, PUT, requestEntity, Object.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @ParameterizedTest
    @MethodSource("getInvalidCategoryRequest")
    void givenInvalidCategoryRequest_whenPutCategories_thenBadRequest(CategoryRequest request) {
        //when
        String url = "/categories/3";
        ResponseEntity<Object> response = restTemplate.exchange(url, PUT, null, Object.class);

        //then
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void givenNotEmptyCategory_whenDeleteCategory_thenNotDeleted() {
        //when
        String url = "/categories/{id}";
        ResponseEntity<Object> deleteResponse =
                restTemplate.exchange(url, DELETE, null, Object.class, 3);
        ResponseEntity<Object> getResponse =
                restTemplate.getForEntity(url, Object.class, 3);

        //then
        assertEquals(400, deleteResponse.getStatusCodeValue());
        assertEquals(200, getResponse.getStatusCodeValue());
    }

    @Test
    void givenNotEmptyCategory_whenDeleteCategoryForced_thenDeleted() {
        //when
        String deleteCategoryUrl = "/categories/3?forced=true";
        ResponseEntity<Object> deleteResponse = restTemplate.exchange(deleteCategoryUrl, DELETE, null, Object.class);

        //and when
        String getCategoryUrl = "/categories/{id}";
        ResponseEntity<CategoryResponse> getResponse = restTemplate.getForEntity(getCategoryUrl, CategoryResponse.class, 3);

        //then
        assertEquals(204, deleteResponse.getStatusCodeValue());
        assertEquals(404, getResponse.getStatusCodeValue());
    }

    @Test
    void givenSubcategories_whenGetSubcategories_thenOk() {
        //when
        String url = "/categories/1/subcategories";
        ResponseEntity<CategoryResponse[]> response = restTemplate.getForEntity(url, CategoryResponse[].class);
        CategoryResponse[] subcategories = response.getBody();

        //then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, subcategories.length);
    }

    @Test
    void givenNoSubcategories_whenGetSubcategories_thenNotFound() {
        //when
        String url = "/categories/111/subcategories";
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenCategory_whenPostSubcategory_thenCreated() {
        //given
        CategoryRequest request = new CategoryRequest("title", "description");

        //when
        String url = "/categories/2/subcategories";
        ResponseEntity<CategoryResponse> response = restTemplate.postForEntity(url, request, CategoryResponse.class);
        URI location = response.getHeaders().getLocation();
        CategoryResponse subcategory = response.getBody();

        //then
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(location);
        assertNotNull(subcategory.getId());
        assertEquals("title", subcategory.getTitle());
        assertEquals("description", subcategory.getDescription());
        assertEquals(subcategory, restTemplate.getForEntity(location, CategoryResponse.class).getBody());
    }

    @Test
    void givenNoCategory_whenPostSubcategory_thenNotFound() {
        //given
        CategoryRequest request = new CategoryRequest("title", "description");

        //when
        String url = "/categories/222/subcategories";
        ResponseEntity<CategoryResponse> response = restTemplate.postForEntity(url, request, CategoryResponse.class);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @ParameterizedTest
    @MethodSource("getInvalidCategoryRequest")
    void givenInvalidRequest_whenPostSubcategories_thenBadRequest(CategoryRequest request) {
        //when
        String url = "/categories/1/subcategories";
        ResponseEntity<CategoryResponse> response = restTemplate.postForEntity(url, request, CategoryResponse.class);

        //then
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void givenProduct_whenPostProductToCategory_thenOk() {
        //given
        String code = "fuji";

        //when
        String url = "/categories/4/products?code={code}";
        ResponseEntity<Object> response = restTemplate.postForEntity(url, null, Object.class, code);

        //then
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void givenNoProduct_whenPostProductToCategory_thenNotFound() {
        //given
        String code = "not-found";

        //when
        String url = "/categories/4/products?code={code}";
        ResponseEntity<Object> response = restTemplate.postForEntity(url, null, Object.class, code);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenNoCategory_whenPostProductToCategory_thenNotFound() {
        //given
        String code = "fuji";

        //when
        String url = "/categories/555/products?code={code}";
        ResponseEntity<Object> response = restTemplate.postForEntity(url, null, Object.class, code);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenProduct_whenDeleteProductFromCategory_thenOk() {
        //given
        String code = "macbook";

        //when
        String url = "/categories/3/products?code={code}";
        ResponseEntity<Object> response = restTemplate.exchange(url, DELETE, null, Object.class, code);

        //then
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void givenNoProduct_whenDeleteProductFromCategory_thenNotFound() {
        //given
        String code = "not-found";

        //when
        String url = "/categories/3/products?code={code}";
        ResponseEntity<Object> response = restTemplate.exchange(url, DELETE, null, Object.class, code);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void givenNoCategory_whenDeleteProductToCategory_thenNotFound() {
        //given
        String code = "macbook";

        //when
        String url = "/categories/555/products?code={code}";
        ResponseEntity<Object> response = restTemplate.exchange(url, DELETE, null, Object.class, code);

        //then
        assertEquals(404, response.getStatusCodeValue());
    }

}