package com.mikhailkarpov.eshop.orders.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orders")
public class TestController {

    @GetMapping
    public String hello(@RequestParam(defaultValue = "Stranger") String name) {
        return String.format("Hello, %s!", name);
    }
}
