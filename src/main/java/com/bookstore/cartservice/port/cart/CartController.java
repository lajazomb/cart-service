package com.bookstore.cartservice.port.cart;

import com.bookstore.cartservice.core.domain.model.Cart;
import com.bookstore.cartservice.core.domain.service.interfaces.ICartService;
import com.bookstore.cartservice.port.cart.exception.CartNotFoundException;
import com.bookstore.cartservice.port.cart.exception.ErrorCreatingCartException;
import com.bookstore.cartservice.port.cart.exception.ItemNotInCartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class CartController {

    @Autowired
    private ICartService cartService;

    @PostMapping("/cart/{userId}")
    public Cart createCart(@PathVariable Long id) throws ErrorCreatingCartException {
        Cart cart = cartService.createCart(id);
        if (cart == null) {
            throw new ErrorCreatingCartException();
        }
        return cart;
    }

    @GetMapping("/cart/{userId}")
    public Cart getCart(@PathVariable Long userId) throws CartNotFoundException {
        return cartService.getCart(userId);
    }

    @DeleteMapping("/cart/{userId}")
    public boolean clearCart(@PathVariable Long userId) throws CartNotFoundException {
        return cartService.clearCart(userId);
    }

    @PostMapping("/cart/{userId}/{productId}/{quantity}")
    public Cart addToCart(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId, @PathVariable("quantity") int quantity) {
        return cartService.addToCart(userId, productId, quantity);
    }

    @PutMapping("/cart/{userId}/{productId}/{quantity}")
    public Cart updateCart(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId, @PathVariable("quantity") int quantity) throws ItemNotInCartException, CartNotFoundException {
        return cartService.updateCart(userId, productId, quantity);
    }

    @PutMapping("/cart/{userId}/{productId}")
    public Cart removeFromCart(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId) throws ItemNotInCartException, CartNotFoundException {
        return cartService.updateCart(userId, productId, 0);
    }

}
