package com.bookstore.cartservice.port.cart.exception;

public class CartNotFoundException extends Exception{
    public CartNotFoundException() {
        super("Cart not found.");
    }
}
