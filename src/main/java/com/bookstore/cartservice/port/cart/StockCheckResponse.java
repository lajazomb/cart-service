package com.bookstore.cartservice.port.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockCheckResponse {

    private UUID productId;
    private int quantity;
    private boolean inStock;


}
