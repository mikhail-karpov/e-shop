package com.mikhailkarpov.eshop.productservice.exception;

public class OrderReservationException extends Exception {

    public OrderReservationException(String message) {
        super(message);
    }

    public OrderReservationException(Throwable cause) {
        super(cause);
    }
}
