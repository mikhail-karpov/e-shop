package com.mikhailkarpov.eshop.shoppingcartservice.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhailkarpov.eshop.shoppingcartservice.config.CookieConfig;
import com.mikhailkarpov.eshop.shoppingcartservice.service.ProductService;
import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.Product;
import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.ShoppingCart;
import com.mikhailkarpov.eshop.shoppingcartservice.web.dto.ShoppingCartItem;
import com.mikhailkarpov.eshop.shoppingcartservice.web.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    public static final String SHOPPING_CART = "SHOPPING_CART";

    private final ProductService productService;

    private final ObjectMapper objectMapper;

    private final CookieConfig cookieConfig;

    @GetMapping
    public ShoppingCart<Product> getShoppingCart(@CookieValue(value = SHOPPING_CART, required = false) Cookie cartCookie,
                                                 HttpServletResponse response) {

        if (cartCookie == null) {
            return new ShoppingCart<>(Collections.emptyList());
        }

        log.info("Request for a shopping-cart with cookie {}", cartCookie.getValue());

        List<ShoppingCartItem> items = getItems(cartCookie);
        ShoppingCart<Product> shoppingCart = buildShoppingCart(items);
        addCookie(response, items);
        return shoppingCart;
    }

    @PostMapping
    public ShoppingCart<Product> saveShoppingCart(@Valid @RequestBody ShoppingCart<ShoppingCartItem> request,
                                                  HttpServletResponse response) {

        log.info("Request to create shopping cart: {}", request);

        ShoppingCart<Product> shoppingCart = buildShoppingCart(request.getItems());
        addCookie(response, request.getItems());
        return shoppingCart;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearShoppingCart(HttpServletResponse response) {

        log.info("Request to delete shopping cart");
        deleteCookie(response);
    }

    private List<ShoppingCartItem> getItems(@NotNull Cookie cookie) {

        try {
            String cookieValue = URLDecoder.decode(cookie.getValue(), "UTF-8");
            ShoppingCartItem[] items = objectMapper.readValue(cookieValue, ShoppingCartItem[].class);
            return Arrays.asList(items);

        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            log.warn("Failed to read shopping cart cookie");
            return Collections.emptyList();
        }
    }

    private ShoppingCart<Product> buildShoppingCart(List<ShoppingCartItem> items) {

        List<Product> products = new ArrayList<>();

        items.forEach(item -> {
            String code = item.getProductId();
            Product product = productService.getProductByCode(code).orElseThrow(() -> {
                String message = String.format("Product with code=%s not found", code);
                return new ProductNotFoundException(message);
            });
            product.setQuantity(item.getQuantity());
            products.add(product);
        });

        return new ShoppingCart<>(products);
    }

    private void addCookie(HttpServletResponse response, List<ShoppingCartItem> items) {

        try {
            String cookieValue = URLEncoder.encode(objectMapper.writeValueAsString(items), "UTF-8");

            Cookie cookie = new Cookie(SHOPPING_CART, cookieValue);
            cookie.setMaxAge(60 * 60 * cookieConfig.getAgeHours());
            response.addCookie(cookie);

        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            log.warn("Failed to write shopping cart cookie: {}", e.getMessage());
            deleteCookie(response);
        }
    }

    private void deleteCookie(HttpServletResponse response) {

        Cookie cookie = new Cookie(SHOPPING_CART, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
