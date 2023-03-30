package com.bookstore.cartservice.port.cart.exception;

public class NotAuthorizedException extends Exception {


    public NotAuthorizedException() {
        super("Not authorized.");
    }
}