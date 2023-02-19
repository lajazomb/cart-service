package com.bookstore.cartservice.port.cart.advice;

import com.bookstore.cartservice.port.cart.exception.ErrorAddingToCartException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorAddingToCartAdvice {

    @ResponseBody
    @ExceptionHandler(value = ErrorAddingToCartException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String errorAddingToCartHandler(ErrorAddingToCartException exception) {
        return exception.getMessage();
    }
}
