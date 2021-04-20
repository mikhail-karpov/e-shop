package com.mikhailkarpov.eshop.orders.services;

import com.mikhailkarpov.eshop.orders.dto.AddressDTO;
import com.mikhailkarpov.eshop.orders.dto.CreateOrderRequest;
import com.mikhailkarpov.eshop.orders.dto.OrderDTO;
import com.mikhailkarpov.eshop.orders.dto.OrderItem;
import com.mikhailkarpov.eshop.orders.entities.AddressEntity;
import com.mikhailkarpov.eshop.orders.entities.OrderEntity;
import com.mikhailkarpov.eshop.orders.entities.OrderStatus;
import com.mikhailkarpov.eshop.orders.entities.OrderStatusType;
import com.mikhailkarpov.eshop.orders.events.OrderPlacedEvent;
import com.mikhailkarpov.eshop.orders.exceptions.OrderNotFoundException;
import com.mikhailkarpov.eshop.orders.repositories.OrderEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderEntityRepository orderEntityRepository;

    @Mock
    private ProductService productService;

    private OrderService orderService;

    @BeforeEach
    public void setOrderService() {
        orderService = new OrderServiceImpl(orderEntityRepository, productService);
    }

    @Test
    void givenRequest_whenPlacedOrder_thenSavedEntityAndPublishedEvent() {
        //given
        AddressDTO address = new AddressDTO();
        address.setId(UUID.randomUUID());
        address.setZip("zip");
        address.setCountry("Country");
        address.setState("State");
        address.setCity("City");
        address.setStreet("Street");
        address.setPhone("phone");

        OrderItem firstItem = new OrderItem();
        firstItem.setCode("abc");
        firstItem.setQuantity(2);

        OrderItem secondItem = new OrderItem();
        secondItem.setCode("xyz");
        secondItem.setQuantity(3);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setShippingAddress(address);
        request.setItems(Arrays.asList(firstItem, secondItem));

        //when
        UUID orderId = UUID.randomUUID();
        orderService.placeOrder(orderId, "customer-id", request);

        //then
        verify(orderEntityRepository).save(any(OrderEntity.class));
        verifyNoMoreInteractions(orderEntityRepository, productService);
    }

    @Test
    void givenEntityExists_whenFindById_thenFound() {
        //given
        UUID id = UUID.randomUUID();

        OrderEntity mockEntity = getMockEntity();

        when(orderEntityRepository.findById(id)).thenReturn(Optional.of(mockEntity));

        //when
        OrderDTO orderDTO = orderService.findOrderById(id);

        //then
        assertNotNull(orderDTO);
        verify(orderEntityRepository).findById(id);
        verifyNoMoreInteractions(orderEntityRepository, productService);
    }

    @Test
    void givenEntityDoesNotExist_whenFindById_thenThrows() {
        //given
        UUID id = UUID.randomUUID();
        when(orderEntityRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThrows(OrderNotFoundException.class, () -> orderService.findOrderById(id));
        verify(orderEntityRepository).findById(id);
        verifyNoMoreInteractions(orderEntityRepository, productService);
    }

    @Test
    void givenEntitiesExists_whenFindAll_thenFound() {
        //given
        OrderEntity mockEntity = getMockEntity();
        when(orderEntityRepository.findAll()).thenReturn(Arrays.asList(mockEntity));

        //when
        List<OrderDTO> dtoList = orderService.findAll();

        //then
        assertEquals(1, dtoList.size());
        verify(orderEntityRepository).findAll();
        verifyNoMoreInteractions(orderEntityRepository, productService);
    }

    private OrderEntity getMockEntity() {
        OrderEntity mockEntity = mock(OrderEntity.class);

        when(mockEntity.getShippingAddress()).thenReturn(mock(AddressEntity.class));
        when(mockEntity.getStatus()).thenReturn(new OrderStatus(OrderStatusType.PLACED, "comment"));

        return mockEntity;
    }
}