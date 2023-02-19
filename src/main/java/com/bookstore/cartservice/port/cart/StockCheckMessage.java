package com.bookstore.cartservice.port.cart;

import lombok.*;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockCheckMessage {

    @Getter private UUID productId;
    @Getter private int quantity;

}