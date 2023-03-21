package com.bookstore.cartservice.core.domain.service.interfaces;

import com.bookstore.cartservice.core.domain.model.Cart;
import com.bookstore.cartservice.port.cart.exception.CartNotFoundException;
import com.bookstore.cartservice.port.cart.exception.ItemNotInCartException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ICartService {

    Cart createCart(String userId);

    Cart addToCart(String userId, UUID productId, int quantity);

    Cart updateCart(String userId, UUID productId, int quantity) throws CartNotFoundException, ItemNotInCartException;

    boolean clearCart(String userId) throws CartNotFoundException;

    Cart getCart(String userId) throws CartNotFoundException;

}
