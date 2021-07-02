package com.mikhailkarpov.eshop.productservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicate product code")
public class ProductDuplicateCodeException extends RuntimeException {

    public ProductDuplicateCodeException(String message) {
        super(message);
    }
}
