package com.mikhailkarpov.eshop.orders.controllers;

import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequestBody;
import com.mikhailkarpov.eshop.orders.dto.OrderDTO;
import com.mikhailkarpov.eshop.orders.dto.PagedResult;
import com.mikhailkarpov.eshop.orders.dto.SearchOrdersRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Collections;

import static com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus.ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureJsonTesters
class OrderControllerTest extends OrderControllerAbstractTest {

    @Autowired
    private JacksonTester<OrderDTO> orderDTOJacksonTester;

    @Autowired
    private JacksonTester<PagedResult<OrderDTO>> pagedResultJacksonTester;

    @Test
    void givenRequestAndJwt_whenPostOrders_thenCreated() throws Exception {
        //given
        CreateOrderRequestBody request = new CreateOrderRequestBody();
        request.setItems(Arrays.asList(abcItem, xyzItem));
        request.setShippingAddress(addressDTO);

        when(orderService.createOrder(orderOwner, request)).thenReturn(id);

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/orders")
                .with(jwt().jwt(jwt -> jwt.subject(orderOwner)))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getHeader("Location")).isEqualTo("http://localhost/orders/" + id);
        verify(orderService).createOrder(orderOwner, request);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenOrders_whenGetOrdersByCustomerAndStatus_thenOk() throws Exception {
        //given
        SearchOrdersRequest searchRequest = new SearchOrdersRequest();
        searchRequest.setStatus(ACCEPTED);
        searchRequest.setCustomerId(orderOwner);

        //and given
        PageRequest pageRequest = PageRequest.of(0, 2);
        PagedResult<OrderDTO> orderResult = new PagedResult<>(new PageImpl(Arrays.asList(orderDTO)));
        when(orderService.searchOrders(searchRequest, pageRequest)).thenReturn(orderResult);

        //when
        MockHttpServletResponse response = mockMvc.perform(get(
                "/orders?customer={customer}&status={status}&size=2", orderOwner, "ACCEPTED")
                .accept(APPLICATION_JSON))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo(pagedResultJacksonTester.write(orderResult).getJson());
        verify(orderService).searchOrders(searchRequest, pageRequest);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenOrders_whenGetOrders_thenOk() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(1, 2);
        Page<OrderDTO> orderPage = new PageImpl(Collections.singletonList(orderDTO), pageRequest, 3);
        PagedResult<OrderDTO> pagedResult = new PagedResult<>(orderPage);
        when(orderService.findAll(pageRequest)).thenReturn(pagedResult);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/orders?page=1&size=2")
                .accept(APPLICATION_JSON))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo(pagedResultJacksonTester.write(pagedResult).getJson());
        verify(orderService).findAll(pageRequest);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenOrderAndRoleADMIN_whenGetOrderById_thenOk() throws Exception {
        //when
        MockHttpServletResponse response = mockMvc.perform(get("/orders/{id}", id)
                .accept(APPLICATION_JSON))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo(orderDTOJacksonTester.write(orderDTO).getJson());
        verify(orderService).findOrderById(id);
    }

    @Test
    void givenOrderAndSubject_whenGetOrderById_thenOk() throws Exception {
        //when
        MockHttpServletResponse response = mockMvc.perform(get("/orders/{id}", id)
                .with(jwt().jwt(jwt -> jwt.subject(orderOwner)))
                .accept(APPLICATION_JSON))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo(orderDTOJacksonTester.write(orderDTO).getJson());
        verify(orderService).findOrderById(id);
    }
}