package com.mikhailkarpov.eshop.orders.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhailkarpov.eshop.orders.dto.*;
import com.mikhailkarpov.eshop.orders.services.OrderService;
import com.mikhailkarpov.eshop.orders.utils.PojoUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus.ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureJsonTesters
@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

    @MockBean
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<OrderDTO> orderDTOJacksonTester;

    @Autowired
    private JacksonTester<OrderWithItemsDTO> orderWithItemsDTOJacksonTester;

    @Autowired
    private JacksonTester<PagedResult<OrderDTO>> pagedResultJacksonTester;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderItemDTO abcItem = new OrderItemDTO("abc", 2);
    private final OrderItemDTO xyzItem = new OrderItemDTO("xyz", 3);
    private final AddressDTO addressDTO = PojoUtils.getValidAddressDTO();
    private final UUID id = UUID.randomUUID();
    private final OrderDTO orderDTO = new OrderDTO(id, "customerId", ACCEPTED, addressDTO);

    @Test
    void givenValidRequest_whenPostOrders_thenCreated() throws Exception {
        //given
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(Arrays.asList(abcItem, xyzItem));
        request.setShippingAddress(addressDTO);

        //and given
        when(orderService.createOrder(anyString(), eq(request))).thenReturn(id);

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/orders")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getHeader("Location")).isEqualTo("http://localhost/orders/" + id);
        verify(orderService).createOrder(anyString(), eq(request));
    }

    @Test
    void givenEmptyRequest_whenPostOrders_thenBadRequest() throws Exception {
        //given
        CreateOrderRequest request = new CreateOrderRequest();

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/orders")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(400);
        verifyNoInteractions(orderService);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("getInvalidItems")
    void givenInvalidItems_whenPostOrders_thenBadRequest(List<OrderItemDTO> items) throws Exception {
        //given
        CreateOrderRequest request = new CreateOrderRequest();
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
        verifyNoInteractions(orderService);
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
        CreateOrderRequest request = new CreateOrderRequest();
        request.setShippingAddress(addressDTO);
        request.setItems(Arrays.asList(abcItem));

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/orders")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(400);
        verifyNoInteractions(orderService);
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

    @Test
    void givenOrdersAndRequestByCustomerIdAndStatus_whenGetOrders_thenOk() throws Exception {
        //given
        SearchOrdersRequest searchRequest = new SearchOrdersRequest();
        searchRequest.setStatus(ACCEPTED);
        searchRequest.setCustomerId("customerId");

        //and given
        PageRequest pageRequest = PageRequest.of(0, 2);
        PagedResult<OrderDTO> orderResult = new PagedResult<>(new PageImpl(Arrays.asList(orderDTO)));
        when(orderService.searchOrders(searchRequest, pageRequest)).thenReturn(orderResult);

        //when
        MockHttpServletResponse response = mockMvc.perform(get(
                "/orders?customer={customer}&status={status}&size=2", "customerId", "ACCEPTED")
                .accept(APPLICATION_JSON))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo(pagedResultJacksonTester.write(orderResult).getJson());
        verify(orderService).searchOrders(searchRequest, pageRequest);
    }

    @Test
    void givenOrders_whenGetOrders_thenOk() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(1, 2);
        PagedResult pagedResult = new PagedResult<>(new PageImpl(Arrays.asList(orderDTO), pageRequest, 3));
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
    void givenOrder_whenGetOrderById_thenOk() throws Exception {
        //given
        OrderWithItemsDTO orderDTO =
                new OrderWithItemsDTO(id, "customerId", ACCEPTED, addressDTO, Arrays.asList(abcItem, xyzItem));
        when(orderService.findOrderById(id)).thenReturn(orderDTO);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/orders/{id}", id)
                .accept(APPLICATION_JSON))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo(orderWithItemsDTOJacksonTester.write(orderDTO).getJson());
        verify(orderService).findOrderById(id);
    }
}