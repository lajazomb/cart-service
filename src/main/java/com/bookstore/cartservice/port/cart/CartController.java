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
    public Cart createCart(@RequestBody UserDto userDto) throws ErrorCreatingCartException {
        Cart cart = cartService.createCart(userDto.getUserId());
        if (cart == null) {
            throw new ErrorCreatingCartException();
        }
        return cart;
    }

    @GetMapping("cart")
    public Cart getCart(@RequestBody UserDto userDto) throws CartNotFoundException {
        return cartService.getCart(userDto.getUserId());
    }

    @DeleteMapping("cart")
    public boolean clearCart(@RequestBody UserDto userDto) throws CartNotFoundException {
        return cartService.clearCart(userDto.getUserId());
    }


    @PostMapping("cart")
    public Cart addToCart(@RequestBody AddToCartRequest addToCartRequest) throws ErrorAddingToCartException, ProductOutOfStockException {

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
            return cartService.addToCart(addToCartRequest.getUserId(), addToCartRequest.getProductId(), addToCartRequest.getQuantity());
        }
        throw new ProductOutOfStockException();
    }

    @PutMapping("cart/{userId}/{productId}/{quantity}")
    public Cart updateCart(@RequestBody UpdateCartRequest updateCartRequest) throws ItemNotInCartException, CartNotFoundException {
        return cartService.updateCart(updateCartRequest.getUserId(), updateCartRequest.getProductId(), updateCartRequest.getQuantity());
    }

    @PutMapping("cart/{userId}/{productId}")
    public Cart removeFromCart(@RequestBody RemoveFromCartRequest removeFromCartRequest) throws ItemNotInCartException, CartNotFoundException {
        return cartService.updateCart(removeFromCartRequest.getUserId(), removeFromCartRequest.getProductId(), 0);
    }

}
