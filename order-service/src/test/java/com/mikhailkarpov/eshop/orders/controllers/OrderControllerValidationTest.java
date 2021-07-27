package com.mikhailkarpov.eshop.orders.controllers;

import com.mikhailkarpov.eshop.orders.dto.AddressDTO;
import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequestBody;
import com.mikhailkarpov.eshop.orders.dto.OrderItemDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WithMockUser
class OrderControllerValidationTest extends OrderControllerAbstractTest {

    @AfterEach
    void verifyNoInteractionsOrderService() {
        verifyNoInteractions(orderService);
    }

    @Test
    void givenEmptyRequest_whenPostOrders_thenBadRequest() throws Exception {
        //given
        CreateOrderRequestBody request = new CreateOrderRequestBody();

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/orders")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("getInvalidItems")
    void givenInvalidItems_whenPostOrders_thenBadRequest(List<OrderItemDTO> items) throws Exception {
        //given
        CreateOrderRequestBody request = new CreateOrderRequestBody();
        request.setShippingAddress(addressDTO);
        request.setItems(items);

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/orders")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    private static Stream<Arguments> getInvalidItems() {
        return Stream.of(
                Arguments.of(Collections.emptyList()),
                Arguments.of(Collections.singletonList(new OrderItemDTO())),
                Arguments.of(Collections.singletonList(new OrderItemDTO("abc", null))),
                Arguments.of(Collections.singletonList(new OrderItemDTO("abc", 0))),
                Arguments.of(Collections.singletonList(new OrderItemDTO("abc", -1))),
                Arguments.of(Collections.singletonList(new OrderItemDTO("", 2))),
                Arguments.of(Collections.singletonList(new OrderItemDTO(null, 2)))
        );
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("getInvalidAddress")
    void givenInvalidAddress_whenPostOrders_thenBadRequest(AddressDTO addressDTO) throws Exception {
        //given
        CreateOrderRequestBody request = new CreateOrderRequestBody();
        request.setShippingAddress(addressDTO);
        request.setItems(Collections.singletonList(abcItem));

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/orders")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(400);
    }

    private static Stream<Arguments> getInvalidAddress() {
        return Stream.of(
                Arguments.of(AddressDTO.builder().build()),
                Arguments.of(AddressDTO.builder().firstName("firstName").build()),
                Arguments.of(AddressDTO.builder().lastName("lastName").build()),
                Arguments.of(AddressDTO.builder().zip("zip").build()),
                Arguments.of(AddressDTO.builder().country("Country").build()),
                Arguments.of(AddressDTO.builder().city("City").build()),
                Arguments.of(AddressDTO.builder().street("Street").build()),
                Arguments.of(AddressDTO.builder().phone("Phone").build())
        );
    }
}