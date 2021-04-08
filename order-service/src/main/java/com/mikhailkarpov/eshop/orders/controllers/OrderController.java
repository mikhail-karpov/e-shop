package com.mikhailkarpov.eshop.orders.controllers;

import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequest;
import com.mikhailkarpov.eshop.orders.dto.ProductDTO;
import com.mikhailkarpov.eshop.orders.entities.OrderEntity;
import com.mikhailkarpov.eshop.orders.services.OrderCommandService;
import com.mikhailkarpov.eshop.orders.services.OrderQueryService;
import com.mikhailkarpov.eshop.orders.services.ProductQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderCommandService orderCommandService;

    private final OrderQueryService orderQueryService;

    private final ProductQueryService productQueryService;

    @GetMapping("/orders")
    public List<OrderEntity> findAllOrders() {

        return orderQueryService.findAll();
    }

    @PostMapping("/orders")
    public ResponseEntity<Object> placeOrder(@Valid @RequestBody CreateOrderRequest request,
                                             UriComponentsBuilder uriComponentsBuilder) {

        String orderId = orderCommandService.placeOrder(request);
        URI location = uriComponentsBuilder.path("/order/{id}").build(orderId);
        return ResponseEntity.accepted().body(Collections.singletonMap("path", location.toString()));
    }

    @GetMapping("/orders/{id}")
    public OrderEntity findOrderById(@PathVariable("id") String orderId) {

        return orderQueryService.findOrderById(orderId);
    }

    @GetMapping("/order/{id}/products")
    public List<ProductDTO> getProductsByOrderId(@PathVariable("id") String orderId) {

        return productQueryService.findProductsByOrderId(orderId);
    }
}
