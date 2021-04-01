package com.mikhailkarpov.eshop.shoppingcartservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.mikhailkarpov.eshop.shoppingcartservice.client.ProductServiceClient;
import com.mikhailkarpov.eshop.shoppingcartservice.config.ProductServiceMockServerConfig;
import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.Product;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "feign.hystrix.enabled=true")
@Import({ProductServiceMockServerConfig.class})
class ProductServiceImplIntegrationTest {

    @Autowired
    private WireMockServer productServer;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenProductServerOk_whenGetProductByCode_thenPresent() throws JsonProcessingException {
        //given
        Product abc = new Product("abc", "product abc", 1000, 10);

        ResponseDefinitionBuilder response = aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(objectMapper.writeValueAsString(abc));

        productServer.stubFor(get(urlEqualTo("/products/abc")).willReturn(response));

        //when
        Optional<Product> product = productService.getProductByCode("abc");

        //then
        assertTrue(product.isPresent());
        assertThat(abc).usingRecursiveComparison().isEqualTo(product.get());
    }

    @Test
    void givenProductServerTimeOut_whenGetProductByCode_thenFallback() {

        //given
        ResponseDefinitionBuilder response = aResponse().withFixedDelay(60000);
        productServer.stubFor(get(urlEqualTo("/products/timeout")).willReturn(response));

        //when
        Optional<Product> product = productService.getProductByCode("timeout");

        //then
        assertFalse(product.isPresent());
    }

    @Test
    void givenProductServerNotFound_whenGetProductByCode_thenFallback() {

        //given
        ResponseDefinitionBuilder response = aResponse().withStatus(404);
        productServer.stubFor(get(urlEqualTo("/products/not-found")).willReturn(response));

        //when
        Optional<Product> product = productService.getProductByCode("not-found");

        //then
        assertFalse(product.isPresent());
    }

    @Test
    void givenProductServerUnauthorized_whenGetProductByCode_thenThrows() {

        //given
        ResponseDefinitionBuilder response = aResponse().withStatus(401);
        productServer.stubFor(get(urlEqualTo("/products/forbidden")).willReturn(response));

        //then
        assertThrows(HystrixBadRequestException.class,
                () -> productServiceClient.getProductByCode("forbidden"));
    }
}
