package com.bookstore.cartservice.port.cart.advice;

import com.bookstore.cartservice.port.cart.exception.ItemNotInCartException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ItemNotInCartAdvice {

    @ResponseBody
    @ExceptionHandler(value = ItemNotInCartException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String itemNotInCartHandler(ItemNotInCartException exception) {
        return exception.getMessage();
    }
}
