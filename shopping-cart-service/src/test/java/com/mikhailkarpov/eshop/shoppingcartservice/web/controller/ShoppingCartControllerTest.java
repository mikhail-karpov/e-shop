package com.mikhailkarpov.eshop.shoppingcartservice.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhailkarpov.eshop.shoppingcartservice.config.CookieConfig;
import com.mikhailkarpov.eshop.shoppingcartservice.service.ProductService;
import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.Product;
import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.ShoppingCart;
import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.ShoppingCartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.mikhailkarpov.eshop.shoppingcartservice.web.controller.ShoppingCartController.SHOPPING_CART;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ShoppingCartController.class)
class ShoppingCartControllerTest {

    @MockBean
    private ProductService productService;

    @MockBean
    private CookieConfig cookieConfig;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final ShoppingCart<ShoppingCartItem> validRequest = new ShoppingCart<>(Arrays.asList(
            new ShoppingCartItem("abc", 2),
            new ShoppingCartItem("xyz", 3)
    ));

    private final ShoppingCart<ShoppingCartItem> notFoundRequest = new ShoppingCart<>(Arrays.asList(
            new ShoppingCartItem("not-found", 2)
    ));

    private final Product abc = new Product("abc", "product 1", 100, 10);

    private final Product xyz = new Product("xyz", "product 2", 200, 20);

    private String validCookieValue;

    private String notFoundCookieValue;

    @BeforeEach
    void setUp() throws JsonProcessingException, UnsupportedEncodingException {
        validCookieValue = URLEncoder.encode(objectMapper.writeValueAsString(validRequest.getItems()), "UTF-8");
        notFoundCookieValue = URLEncoder.encode(objectMapper.writeValueAsString(notFoundRequest.getItems()), "UTF-8");

        when(cookieConfig.getAgeHours()).thenReturn(2);

        when(productService.getProductByCode("abc")).thenReturn(Optional.of(abc));
        when(productService.getProductByCode("xyz")).thenReturn(Optional.of(xyz));
        when(productService.getProductByCode("not-found")).thenReturn(Optional.empty());
    }

    @Test
    void givenCookie_whenGetShoppingCart_thenOk() throws Exception {

        Cookie cookie = new Cookie(SHOPPING_CART, validCookieValue);
        String expectedCookie = validCookieValue;

        mockMvc.perform(get("/shopping-cart")
                .cookie(cookie)
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(cookie().value(SHOPPING_CART, expectedCookie))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].code").value("abc"))
                .andExpect(jsonPath("$.items[0].title").value("product 1"))
                .andExpect(jsonPath("$.items[0].price").value(100))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[1].code").value("xyz"))
                .andExpect(jsonPath("$.items[1].title").value("product 2"))
                .andExpect(jsonPath("$.items[1].price").value(200))
                .andExpect(jsonPath("$.items[1].quantity").value(3));

        verify(productService, times(2)).getProductByCode(any());
        verifyNoMoreInteractions(productService);
    }

    @Test
    void givenNoCookie_whenGetShoppingCart_thenOk() throws Exception {

        mockMvc.perform(get("/shopping-cart")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(SHOPPING_CART))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.items").isEmpty());

        verifyNoInteractions(productService);
    }

    @Test
    void givenValidRequest_whenPostShoppingCart_thenOk() throws Exception {

        String requestBody = objectMapper.writeValueAsString(validRequest);
        String expectedCookie = validCookieValue;

        mockMvc.perform(post("/shopping-cart")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(cookie().value(SHOPPING_CART, expectedCookie))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].code").value("abc"))
                .andExpect(jsonPath("$.items[0].title").value("product 1"))
                .andExpect(jsonPath("$.items[0].price").value(100))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[1].code").value("xyz"))
                .andExpect(jsonPath("$.items[1].title").value("product 2"))
                .andExpect(jsonPath("$.items[1].price").value(200))
                .andExpect(jsonPath("$.items[1].quantity").value(3));

        verify(productService, times(2)).getProductByCode(any());
        verifyNoMoreInteractions(productService);
    }

    @ParameterizedTest
    @MethodSource("getInvalidRequest")
    void givenInvalidRequest_whenPostShoppingCart_thenBadRequest(ShoppingCart<ShoppingCartItem> request) throws Exception {

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/shopping-cart")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void givenProductNotFound_whenGetShoppingCart_thenNotFound() throws Exception {
        //given
        Cookie cookie = new Cookie(SHOPPING_CART, notFoundCookieValue);

        mockMvc.perform(get("/shopping-cart")
                .cookie(cookie))
                .andExpect(status().isNotFound())
                .andExpect(cookie().doesNotExist(SHOPPING_CART));
    }

    @Test
    void givenProductNotFound_whenPostShoppingCart_thenNotFound() throws Exception {
        //given
        String requestBody = objectMapper.writeValueAsString(notFoundRequest);
        Cookie cookie = new Cookie(SHOPPING_CART, notFoundCookieValue);

        mockMvc.perform(post("/shopping-cart")
                .cookie(cookie)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(cookie().doesNotExist(SHOPPING_CART));
    }

    @Test
    void whenDeleteShoppingCart_thenNoContent() throws Exception {

        mockMvc.perform(delete("/shopping-cart"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge(SHOPPING_CART, 0));
    }

    private static Stream<Arguments> getInvalidRequest() {

        return Stream.of(
                Arguments.of(new ShoppingCart<ShoppingCartItem>(null)),
                Arguments.of(new ShoppingCart<ShoppingCartItem>(Collections.emptyList())),
                Arguments.of(new ShoppingCart<>(Arrays.asList(new ShoppingCartItem(null, null)))),
                Arguments.of(new ShoppingCart<>(Arrays.asList(new ShoppingCartItem(null, 0))))
        );
    }
}