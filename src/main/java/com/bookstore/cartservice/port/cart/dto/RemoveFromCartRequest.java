package com.bookstore.cartservice.port.cart.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RemoveFromCartRequest { //user product quantity
    private String userId;
    private UUID productId;
}
