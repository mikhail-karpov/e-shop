package com.mikhailkarpov.eshop.productservice.persistence.entity;

import com.mikhailkarpov.eshop.productservice.exception.ProductReservationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void initProduct() {
        product = new Product("abc", "title", "description", 100, 5);
    }

    @Test
    void givenProductAvailableForReservation_whenAddReserve_thenReserved() throws ProductReservationException {
        //when
        product.addReserved(5);

        //then
        Assertions.assertEquals(5, product.getReserved());
    }

    @Test
    void givenProductNotAvailableForReservation_whenAddReserve_thenThrown() throws ProductReservationException {
        //when
        Assertions.assertThrows(ProductReservationException.class, () -> product.addReserved(15));

        //then
        Assertions.assertEquals(0, product.getReserved());
    }
}