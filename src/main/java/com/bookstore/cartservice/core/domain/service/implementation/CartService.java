package com.bookstore.cartservice.core.domain.service.implementation;

import com.bookstore.cartservice.core.domain.model.Cart;
import com.bookstore.cartservice.core.domain.service.interfaces.ICartRepository;
import com.bookstore.cartservice.core.domain.service.interfaces.ICartService;
import com.bookstore.cartservice.port.cart.exception.CartNotFoundException;
import com.bookstore.cartservice.port.cart.exception.ItemNotInCartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartService implements ICartService{

    @Autowired
    private ICartRepository cartRepository;
    @Override
    public Cart createCart(Long userId) {
        Map<Long, Integer> items = new HashMap<>();
        Cart cart = new Cart(userId, items);
        return cartRepository.save(cart);
    }

    @Override
    public Cart addToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = createCart(userId);
        }
        Map<Long, Integer> tempCart = cart.getItems();
        tempCart.putIfAbsent(productId, quantity); //what happens if item is already in cart ?
        cart.setItems(tempCart);
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public Cart updateCart(Long userId, Long productId, int quantity) throws CartNotFoundException, ItemNotInCartException {
        Cart tempCart = cartRepository.findByUserId(userId);
        if (tempCart == null) {
            throw new CartNotFoundException();
        }
        Map<Long, Integer> tempItems = tempCart.getItems();
        if (!tempItems.containsKey(productId)) {
            throw new ItemNotInCartException();
        }
        tempItems.put(productId, quantity);
        tempCart.setItems(tempItems);
        return tempCart;
    }

    @Override
    public boolean clearCart(Long userId) throws CartNotFoundException {
        if (cartRepository.findByUserId(userId) != null) {
            Cart cart = cartRepository.findByUserId(userId);
            cart.setItems(new HashMap<Long, Integer>());
            cartRepository.save(cart);
        } else {
            throw new CartNotFoundException();
        }
        return true; //should it be void instead, as error handling would be done through the cartNotFoundException
    }

    @Override
    public Cart getCart(Long userId) throws CartNotFoundException {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        return cart;
    }
}
