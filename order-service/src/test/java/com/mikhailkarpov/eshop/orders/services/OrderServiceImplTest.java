package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.*;
import com.mikhailkarpov.eshop.orders.exceptions.OrderNotFoundException;
import com.mikhailkarpov.eshop.orders.persistence.entities.Address;
import com.mikhailkarpov.eshop.orders.persistence.entities.Order;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderItem;
import com.mikhailkarpov.eshop.orders.persistence.entities.OrderStatus;
import com.mikhailkarpov.eshop.orders.persistence.repositories.OrderRepository;
import com.mikhailkarpov.eshop.orders.utils.DtoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private final AddressDTO addressDTO = DtoUtils.getValidAddress();

    private final List<OrderItemDTO> itemDTOList = Arrays.asList(
            new OrderItemDTO("abc", 5),
            new OrderItemDTO("xyz", 3)
    );

    private final OrderItem item = new OrderItem("abc", 5);

    private final Order order =
            new Order("customerId", Mockito.mock(Address.class), OrderStatus.ACCEPTED, Collections.singleton(item));

    @Test
    void givenRequest_whenCreateOrder_thenUUIDReturned() {
        //given
        CreateOrderRequest request = new CreateOrderRequest();
        request.setShippingAddress(addressDTO);
        request.setItems(itemDTOList);

        UUID expectedId = UUID.randomUUID();
        Order mockOrder = Mockito.mock(Order.class);
        when(mockOrder.getId()).thenReturn(expectedId);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        //when
        UUID actualId = orderService.createOrder("customerId", request);

        //then
        assertEquals(expectedId, actualId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void givenRequest_whenSearchOrders_thenFound() {
        //given
        SearchOrdersRequest request = new SearchOrdersRequest();
        request.setCustomerId("customerId");
        request.setStatus(OrderStatus.ACCEPTED);

        PageRequest pageRequest = PageRequest.of(1, 2);

        when(orderRepository.findAll(any(Specification.class), eq(pageRequest)))
                .thenReturn(new PageImpl(Arrays.asList(order), pageRequest, 3));

        //when
        PagedResult<OrderDTO> pagedResult = orderService.searchOrders(request, pageRequest);
        List<OrderDTO> ordersDto = pagedResult.getContent();

        //then
        assertEquals(1, ordersDto.size());
        assertEquals("customerId", ordersDto.get(0).getCustomerId());
        assertEquals(OrderStatus.ACCEPTED, ordersDto.get(0).getStatus());
        Assertions.assertNotNull(ordersDto.get(0).getShippingAddress());
        assertEquals(order.getId(), ordersDto.get(0).getId());

        assertEquals(1, pagedResult.getPage());
        assertEquals(2, pagedResult.getTotalPages());
        assertEquals(3, pagedResult.getTotalElements());

        verify(orderRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void givenOrders_whenFindAll_thenFound() {
        //given
        PageRequest pageRequest = PageRequest.of(1, 2);
        when(orderRepository.findAll(pageRequest))
                .thenReturn(new PageImpl<>(Arrays.asList(order), pageRequest, 3));

        //when
        PagedResult<OrderDTO> pagedResult = orderService.findAll(pageRequest);
        List<OrderDTO> ordersDto = pagedResult.getContent();

        //then
        assertEquals(1, ordersDto.size());
        assertEquals("customerId", ordersDto.get(0).getCustomerId());
        assertEquals(OrderStatus.ACCEPTED, ordersDto.get(0).getStatus());
        Assertions.assertNotNull(ordersDto.get(0).getShippingAddress());
        assertEquals(order.getId(), ordersDto.get(0).getId());

        assertEquals(1, pagedResult.getPage());
        assertEquals(2, pagedResult.getTotalPages());
        assertEquals(3, pagedResult.getTotalElements());

        verify(orderRepository).findAll(eq(pageRequest));
    }

    @Test
    void givenOrder_whenFindOrderById_thenFound() {
        //given
        UUID id = order.getId();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        //when
        OrderWithItemsDTO found = orderService.findOrderById(id);

        //then
        assertEquals("customerId", found.getCustomerId());
        assertEquals(id, found.getId());
        assertEquals(OrderStatus.ACCEPTED, found.getStatus());
        Assertions.assertNotNull(found.getShippingAddress());
        assertEquals(1, found.getItems().size());
        assertEquals("abc", found.getItems().get(0).getCode());
        assertEquals(5, found.getItems().get(0).getQuantity());

        verify(orderRepository).findById(id);
    }

    @Test
    void givenNoOrder_whenFindOrderById_thenThrown() {
        //given
        UUID id = order.getId();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.findOrderById(id));

        verify(orderRepository).findById(id);
    }

    @Test
    void givenOrder_whenUpdateOrderStatus_thenUpdated() {
        //given
        UUID id = order.getId();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        //when
        orderService.updateOrderStatus(id, OrderStatus.CANCELED);

        //then
        Assertions.assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(orderRepository).findById(id);

        order.setStatus(OrderStatus.ACCEPTED);
    }

    @Test
    void givenNoOrder_whenUpdateOrderStatus_thenThrown() {
        //given
        UUID id = order.getId();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderStatus(id, OrderStatus.CANCELED));
    }
}