package com.mikhailkarpov.eshop.productservice.service;

import com.mikhailkarpov.eshop.productservice.exception.OrderReservationException;
import com.mikhailkarpov.eshop.productservice.messaging.dto.OrderItem;
import com.mikhailkarpov.eshop.productservice.persistence.entity.Product;
import com.mikhailkarpov.eshop.productservice.persistence.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class OrderReservationServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderReservationServiceImpl orderReservationService;

    @Captor
    private ArgumentCaptor<Product> productArgumentCaptor;

    private final Product product = new Product("abc", "title", "desc", 100, 4);

    private final OrderItem item = new OrderItem("abc", 3);

    @Test
    void givenProduct_whenReserve_thenReserved() throws OrderReservationException {
        //given
        when(productRepository.findById("abc")).thenReturn(Optional.of(product));

        //when
        orderReservationService.reserve(Collections.singletonList(item));

        //then
        Mockito.verify(productRepository).findById("abc");
        Mockito.verify(productRepository).save(productArgumentCaptor.capture());
        Mockito.verifyNoMoreInteractions(productRepository);
        assertEquals(3, productArgumentCaptor.getValue().getReserved());
    }

    @Test
    void givenNoProduct_whenReserve_thenThrown() {
        //given
        when(productRepository.findById("abc")).thenReturn(Optional.empty());

        //when
        assertThrows(OrderReservationException.class,
                () -> orderReservationService.reserve(Collections.singletonList(item)));

        //then
        Mockito.verify(productRepository).findById("abc");
        Mockito.verifyNoMoreInteractions(productRepository);
    }

    @Test
    void givenNotEnoughProduct_whenReserve_thenThrown() throws OrderReservationException {
        //given
        OrderItem item = new OrderItem("abc", 13);
        when(productRepository.findById("abc")).thenReturn(Optional.of(product));

        //when
        assertThrows(OrderReservationException.class,
                () -> orderReservationService.reserve(Collections.singletonList(item)));

        //then
        Mockito.verify(productRepository).findById("abc");
        Mockito.verifyNoMoreInteractions(productRepository);
    }
}