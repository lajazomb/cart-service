package com.bookstore.cartservice.port.cart;

import com.bookstore.cartservice.core.domain.model.Cart;
import com.bookstore.cartservice.core.domain.service.interfaces.ICartService;
import com.bookstore.cartservice.port.cart.exception.*;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class CartController {

    @Autowired
    private ICartService cartService;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange directExchange;

    public static final String ROUTING_KEY = "cartservice.checkStock";



    @PostMapping("/cart/{userId}")
    public Cart createCart(@PathVariable Long userId) throws ErrorCreatingCartException {
        Cart cart = cartService.createCart(userId);
        if (cart == null) {
            throw new ErrorCreatingCartException();
        }
        return cart;
    }

    @GetMapping("/cart/{userId}")
    public Cart getCart(@PathVariable("userId") Long userId) throws CartNotFoundException {
        return cartService.getCart(userId);
    }

    @DeleteMapping("/cart/{userId}")
    public boolean clearCart(@PathVariable("userId") Long userId) throws CartNotFoundException {
        return cartService.clearCart(userId);
    }


    @PostMapping("/cart/{userId}/{productId}/{quantity}")
    public Cart addToCart(@PathVariable("userId") Long userId,
                          @PathVariable("productId") UUID productId,
                          @PathVariable("quantity") int quantity) throws ErrorAddingToCartException, ProductOutOfStockException {

        StockCheckMessage msg = StockCheckMessage.builder()
                .productId(productId)
                .quantity(quantity)
                .build();

        StockCheckResponse response = template.convertSendAndReceiveAsType(
                "productservice.checkstock",
                ROUTING_KEY,
                msg,
                new ParameterizedTypeReference<>() {
                });

        if (response == null) {
            throw new ErrorAddingToCartException();
        }
        if (response.isInStock()) {
            return cartService.addToCart(userId, productId, quantity);
        }
        throw new ProductOutOfStockException();
    }

    @PutMapping("/cart/{userId}/{productId}/{quantity}")
    public Cart updateCart(@PathVariable("userId") Long userId, @PathVariable("productId") UUID productId, @PathVariable("quantity") int quantity) throws ItemNotInCartException, CartNotFoundException {
        return cartService.updateCart(userId, productId, quantity);
    }

    @PutMapping("/cart/{userId}/{productId}")
    public Cart removeFromCart(@PathVariable("userId") Long userId, @PathVariable("productId") UUID productId) throws ItemNotInCartException, CartNotFoundException {
        return cartService.updateCart(userId, productId, 0);
    }

}
