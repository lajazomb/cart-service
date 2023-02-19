package com.bookstore.cartservice.port.cart.advice;

import com.bookstore.cartservice.port.cart.exception.ProductOutOfStockException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ProductOutOfStockAdvice {

    @ResponseBody
    @ExceptionHandler(value = ProductOutOfStockException.class)
    @ResponseStatus(HttpStatus.OK)
    String productOutOfStockHandler(ProductOutOfStockException exception) {
        return exception.getMessage();
    }

}
