package com.bookstore.cartservice.core.domain.service.interfaces;

import com.bookstore.cartservice.core.domain.model.Cart;
import com.bookstore.cartservice.port.cart.exception.CartNotFoundException;
import com.bookstore.cartservice.port.cart.exception.ItemNotInCartException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ICartService {

    Cart createCart(Long userId);

    Cart addToCart(Long userId, UUID productId, int quantity);

    Cart updateCart(Long userId, UUID productId, int quantity) throws CartNotFoundException, ItemNotInCartException;

    boolean clearCart(Long userId) throws CartNotFoundException;

    Cart getCart(Long userId) throws CartNotFoundException;

}
