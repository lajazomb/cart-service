package com.bookstore.cartservice.port.cart.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCartRequest { //user product quantity
    private UUID userId;
    private UUID productId;
    private int quantity;
}
