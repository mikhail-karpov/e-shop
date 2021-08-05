package com.mikhailkarpov.eshop.orders.controllers;

import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequestBody;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerSecurityTest extends OrderControllerAbstractTest {

    @Test
    void givenNoAuth_whenHitEndpoints_then401() throws Exception {
        //when
        mockMvc.perform(get("/orders"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/orders/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/orders"))
                .andExpect(status().isUnauthorized());

        //then
        verifyNoInteractions(orderService);
    }

    @Test
    @WithMockUser
    void givenNoPermission_whenGetOrders_then403() throws Exception {
        //when
        mockMvc.perform(get("/orders"))
                .andExpect(status().isForbidden());

        //then
        verifyNoInteractions(orderService);
    }

    @Test
    @WithMockUser
    void givenNoPermission_whenGetOrderById_then403() throws Exception {
        //when
        mockMvc.perform(get("/orders/" + id))
                .andExpect(status().isForbidden());

        //then
        verify(orderService).findOrderById(id);
    }

    @Test
    @WithMockUser
    void givenNoJwt_whenPostOrder_then403() throws Exception {
        //given
        CreateOrderRequestBody request = new CreateOrderRequestBody();
        request.setItems(Arrays.asList(abcItem, xyzItem));
        request.setShippingAddress(addressDTO);

        //when
        mockMvc.perform(post("/orders/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        //then
        verifyNoInteractions(orderService);
    }

}