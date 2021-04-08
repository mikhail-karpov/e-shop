package com.mikhailkarpov.eshop.orders.controllers;

import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequest;
import com.mikhailkarpov.eshop.orders.dto.OrderDTO;
import com.mikhailkarpov.eshop.orders.dto.ProductDTO;
import com.mikhailkarpov.eshop.orders.services.OrderService;
import com.mikhailkarpov.eshop.orders.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final ProductService productService;

    @GetMapping("/orders")
    public List<OrderDTO> findAllOrders() {

        return orderService.findAll();
    }

    @PostMapping("/orders")
    public ResponseEntity<Object> placeOrder(@Valid @RequestBody CreateOrderRequest request,
                                             UriComponentsBuilder uriComponentsBuilder) {

        UUID orderId = UUID.randomUUID();
        String customerId = UUID.randomUUID().toString(); // todo extract from JWT
        orderService.placeOrder(orderId, customerId, request);

        URI location = uriComponentsBuilder.path("/order/{id}").build(orderId.toString());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/orders/{id}")
    public OrderDTO findOrderById(@PathVariable("id") String orderId) {

        UUID uuid = UUID.fromString(orderId);
        return orderService.findOrderById(uuid);
    }

    @GetMapping("/orders/{id}/products")
    public List<ProductDTO> getProductsByOrderId(@PathVariable("id") String orderId) {

        UUID uuid = UUID.fromString(orderId);
        return productService.findProductsByOrderId(uuid);
    }
}
