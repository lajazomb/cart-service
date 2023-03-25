package com.bookstore.cartservice.core.domain.service.interfaces;

import com.bookstore.cartservice.core.domain.model.Cart;
import com.bookstore.cartservice.port.cart.exception.CartNotFoundException;
import com.bookstore.cartservice.port.cart.exception.ItemNotInCartException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ICartService {

    Cart createCart(UUID userId);

    Cart addToCart(UUID userId, UUID productId, int quantity);

    Cart updateCart(UUID userId, UUID productId, int quantity) throws CartNotFoundException, ItemNotInCartException;

    boolean clearCart(UUID userId) throws CartNotFoundException;

    Cart getCart(UUID userId) throws CartNotFoundException;

}
