package com.mikhailkarpov.eshop.productservice.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/products")
    public String hello(@RequestParam String name) {
        return String.format("Hello, %s! I'm product-service", name);
    }
}
