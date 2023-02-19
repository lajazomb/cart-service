package com.bookstore.cartservice.port.cart.exception;

public class ErrorAddingToCartException extends Exception {
    public ErrorAddingToCartException() {
        super("Item could not be added to cart.");
    }
}
