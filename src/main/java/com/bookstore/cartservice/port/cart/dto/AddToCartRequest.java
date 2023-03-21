package com.bookstore.cartservice.port.cart.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AddToCartRequest { //user product quantity
    private Long userId;
    private UUID productId;
    private int quantity;
}
