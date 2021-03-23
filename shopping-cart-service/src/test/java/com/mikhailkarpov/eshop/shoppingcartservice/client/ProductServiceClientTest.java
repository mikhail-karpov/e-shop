package com.mikhailkarpov.eshop.shoppingcartservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.mikhailkarpov.eshop.shoppingcartservice.config.ProductServiceMockServerConfig;
import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.Product;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "feign.hystrix.enabled=true")
@Import(ProductServiceMockServerConfig.class)
class ProductServiceClientTest {

    @Autowired
    private WireMockServer productService;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenProductServiceOk_whenGetProductByCode_thenReturned() throws JsonProcessingException {

        //given
        Product abc = new Product("abc", "product abc", 1000, 10);

        ResponseDefinitionBuilder response = aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(objectMapper.writeValueAsString(abc));

        productService.stubFor(get(urlEqualTo("/products/abc")).willReturn(response));

        //when
        Product product = productServiceClient.getProductByCode("abc");

        //then
        Assertions.assertThat(abc).usingRecursiveComparison().isEqualTo(product);
    }

    @Test
    void givenProductServiceTimeOut_whenGetProductByCode_thenFallback() {

        //given
        ResponseDefinitionBuilder response = aResponse().withFixedDelay(20000);
        productService.stubFor(get(urlEqualTo("/products/timeout")).willReturn(response));

        //when
        Product product = productServiceClient.getProductByCode("timeout");

        //then
        Product fallback = new ProductServiceClientFallback().getProductByCode("timeout");
        Assertions.assertThat(fallback).usingRecursiveComparison().isEqualTo(product);
    }

    @Test
    void givenProductServiceNotFound_whenGetProductByCode_thenFallback() {

        //given
        ResponseDefinitionBuilder response = aResponse().withStatus(404);
        productService.stubFor(get(urlEqualTo("/products/not-found")).willReturn(response));

        //then
        Product fallback = new ProductServiceClientFallback().getProductByCode("timeout");
        Assertions.assertThat(fallback).usingRecursiveComparison().isEqualTo(fallback);
    }

    @Test
    void givenProductServiceUnauthorized_whenGetProductByCode_thenThrows() {

        //given
        ResponseDefinitionBuilder response = aResponse().withStatus(401);
        productService.stubFor(get(urlEqualTo("/products/forbidden")).willReturn(response));

        //then
        assertThrows(HystrixBadRequestException.class,
                () -> productServiceClient.getProductByCode("forbidden"));
    }
}