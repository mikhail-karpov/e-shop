package com.mikhailkarpov.eshop.shoppingcartservice.config;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class MyResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HystrixBadRequestException.class)
    protected ResponseEntity<Object> handleHystrixBadRequest(HystrixBadRequestException ex, WebRequest request) {

        Throwable cause = ex.getCause();
        String message = cause == null ? ex.getMessage() : cause.getMessage();
        log.error("Unexpected error occurred: {}", message);

        return handleExceptionInternal(ex, null, new HttpHeaders(), INTERNAL_SERVER_ERROR, request);
    }
}
