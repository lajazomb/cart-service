package com.bookstore.cartservice.core.domain.service.implementation;

import com.bookstore.cartservice.core.domain.model.Cart;
import com.bookstore.cartservice.core.domain.service.interfaces.ICartRepository;
import com.bookstore.cartservice.core.domain.service.interfaces.ICartService;
import com.bookstore.cartservice.port.cart.exception.CartNotFoundException;
import com.bookstore.cartservice.port.cart.exception.ItemNotInCartException;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CartService implements ICartService{

    @Autowired
    private ICartRepository cartRepository;


    @Override
    public Cart createCart(String userId) {
        Map<UUID, Integer> items = new HashMap<>();
        Cart cart = new Cart(userId, items);
        return cartRepository.save(cart);
    }

    @Override
    public Cart addToCart(String userId, UUID productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = createCart(userId);
        }
        Map<UUID, Integer> tempCart = cart.getItems();
        if (tempCart.containsKey(productId)) {
            int previousQuantity = tempCart.get(productId);
            tempCart.put(productId, previousQuantity+quantity);
        } else {
            tempCart.putIfAbsent(productId, quantity);
        }
        cart.setItems(tempCart);
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public Cart updateCart(String userId, UUID productId, int quantity) throws CartNotFoundException, ItemNotInCartException {
        Cart tempCart = cartRepository.findByUserId(userId);
        if (tempCart == null) {
            throw new CartNotFoundException();
        }
        Map<UUID, Integer> tempItems = tempCart.getItems();
        if (!tempItems.containsKey(productId)) {
            throw new ItemNotInCartException();
        }
        tempItems.put(productId, quantity);
        tempCart.setItems(tempItems);
        return tempCart;
    }

    @Override
    public boolean clearCart(String userId) throws CartNotFoundException {
        if (cartRepository.findByUserId(userId) != null) {
            Cart cart = cartRepository.findByUserId(userId);
            cart.setItems(new HashMap<UUID, Integer>());
            cartRepository.save(cart);
        } else {
            throw new CartNotFoundException();
        }
        return true; //should it be void instead, as error handling would be done through the cartNotFoundException
    }

    @Override
    public Cart getCart(String userId) throws CartNotFoundException {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        return cart;
    }
}