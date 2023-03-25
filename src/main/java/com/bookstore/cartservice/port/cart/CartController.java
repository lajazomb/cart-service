package com.bookstore.cartservice.port.cart;

import com.bookstore.cartservice.core.domain.model.Cart;
import com.bookstore.cartservice.core.domain.service.interfaces.ICartService;
import com.bookstore.cartservice.port.cart.dto.AddToCartRequest;
import com.bookstore.cartservice.port.cart.dto.RemoveFromCartRequest;
import com.bookstore.cartservice.port.cart.dto.UpdateCartRequest;
import com.bookstore.cartservice.port.cart.dto.UserDto;
import com.bookstore.cartservice.port.cart.exception.*;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/")
@RestController
public class CartController {

    @Autowired
    private ICartService cartService;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange directExchange;

    public static final String ROUTING_KEY = "cartservice.checkStock";

    @PostMapping("cart/create")
    public ResponseEntity<Cart> createCart(@RequestBody UserDto userDto) throws ErrorCreatingCartException {
        Cart cart = cartService.createCart(userDto.getUserId());
        if (cart == null) {
            throw new ErrorCreatingCartException();
        }
        return ResponseEntity.ok(cart);
    }

    @GetMapping("cart/user/{userid}")
    public ResponseEntity<Cart> getCart(@PathVariable UUID userid) throws CartNotFoundException {
        return ResponseEntity.ok(cartService.getCart(userid));
    }

    @DeleteMapping("cart/user/{userid}")
    public ResponseEntity<UUID> clearCart(@PathVariable UUID userid) throws CartNotFoundException {
        return ResponseEntity.ok(userid);
    }

    @PostMapping("cart")
    public ResponseEntity<Cart> addToCart(@RequestBody AddToCartRequest addToCartRequest) throws ErrorAddingToCartException, ProductOutOfStockException {
        StockCheckMessage msg = StockCheckMessage.builder()
                .productId(addToCartRequest.getProductId())
                .quantity(addToCartRequest.getQuantity())
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
            return ResponseEntity.ok(cartService.addToCart(addToCartRequest.getUserId(), addToCartRequest.getProductId(), addToCartRequest.getQuantity()));
        }
        throw new ProductOutOfStockException();
    }

    @PutMapping("cart/{userId}/{productId}/{quantity}")
    public ResponseEntity<Cart> updateCart(@RequestBody UpdateCartRequest updateCartRequest) throws ItemNotInCartException, CartNotFoundException {
        return ResponseEntity.ok(cartService.updateCart(updateCartRequest.getUserId(), updateCartRequest.getProductId(), updateCartRequest.getQuantity()));
    }

    @PutMapping("cart/{userId}/{productId}")
    public ResponseEntity<Cart> removeFromCart(@RequestBody RemoveFromCartRequest removeFromCartRequest) throws ItemNotInCartException, CartNotFoundException {
        return ResponseEntity.ok(cartService.updateCart(removeFromCartRequest.getUserId(), removeFromCartRequest.getProductId(), 0));
    }
}
