package com.mikhailkarpov.eshop.orders.controllers;

import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequestBody;
import com.mikhailkarpov.eshop.orders.dto.OrderDTO;
import com.mikhailkarpov.eshop.orders.dto.PagedResult;
import com.mikhailkarpov.eshop.orders.dto.SearchOrdersRequest;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import com.mikhailkarpov.eshop.orders.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResult<OrderDTO> findAllOrders(@RequestParam("customer") Optional<String> customerId,
                                               @RequestParam("status") Optional<String> status,
                                               Pageable pageable) {

        if (customerId.isPresent() || status.isPresent()) {
            SearchOrdersRequest request = buildSearchRequest(customerId, status);
            return orderService.searchOrders(request, pageable);
        } else {
            return orderService.findAll(pageable);
        }
    }

    @PostMapping("/orders")
    @PreAuthorize("#jwt != null && #jwt.subject != null")
    public ResponseEntity<Object> createOrder(@Valid @RequestBody CreateOrderRequestBody requestBody,
                                              UriComponentsBuilder uriComponentsBuilder,
                                              @AuthenticationPrincipal Jwt jwt) {

        String customerId = jwt.getSubject();
        UUID orderId = orderService.createOrder(customerId, requestBody);

        URI location = uriComponentsBuilder.path("/orders/{id}").build(orderId.toString());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/orders/{id}")
    @PostAuthorize("hasRole('ADMIN') or (#jwt != null && returnObject.customerId == #jwt.subject)")
    public OrderDTO findOrderById(@PathVariable("id") UUID id,
                                  @AuthenticationPrincipal Jwt jwt) {

        return orderService.findOrderById(id);
    }

    private SearchOrdersRequest buildSearchRequest(Optional<String> customerId, Optional<String> status) {

        SearchOrdersRequest request = new SearchOrdersRequest();

        if (customerId.isPresent()) {
            request.setCustomerId(customerId.get());
        }

        if (status.isPresent()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.get());
                request.setStatus(orderStatus);
            } catch (IllegalArgumentException e) {
                //do nothing
            }
        }
        return request;
    }
}
