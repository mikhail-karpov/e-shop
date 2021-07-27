package com.mikhailkarpov.eshop.orders.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhailkarpov.eshop.orders.dto.AddressDTO;
import com.mikhailkarpov.eshop.orders.dto.OrderDTO;
import com.mikhailkarpov.eshop.orders.dto.OrderItemDTO;
import com.mikhailkarpov.eshop.orders.services.OrderService;
import com.mikhailkarpov.eshop.orders.utils.PojoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus.ACCEPTED;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = OrderController.class)
public class OrderControllerAbstractTest {

    @MockBean
    OrderService orderService;

    @MockBean
    JwtDecoder jwtDecoder;

    @Autowired
    MockMvc mockMvc;

    final ObjectMapper objectMapper = new ObjectMapper();
    final OrderItemDTO abcItem = new OrderItemDTO("abc", 2);
    final OrderItemDTO xyzItem = new OrderItemDTO("xyz", 3);
    final AddressDTO addressDTO = PojoUtils.getValidAddressDTO();
    final UUID id = UUID.randomUUID();
    final String orderOwner = "owner";
    final OrderDTO orderDTO = new OrderDTO(id, orderOwner, ACCEPTED, addressDTO, Collections.singletonList(abcItem));

    @BeforeEach
    void setupOrderService() {
        when(orderService.findOrderById(id)).thenReturn(orderDTO);
    }
}
