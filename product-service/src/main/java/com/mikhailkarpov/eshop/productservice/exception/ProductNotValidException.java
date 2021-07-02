package com.mikhailkarpov.eshop.productservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Product not valid")
public class ProductNotValidException extends RuntimeException {

    public ProductNotValidException(String message) {
        super(message);
    }
}
