package com.bookstore.cartservice.core.domain.service.implementation;

import com.bookstore.cartservice.core.domain.model.Cart;
import com.bookstore.cartservice.core.domain.service.interfaces.ICartRepository;
import com.bookstore.cartservice.port.cart.exception.CartNotFoundException;
import com.bookstore.cartservice.port.cart.exception.ItemNotInCartException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartServiceTests {

    @Mock
    private ICartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private UUID userId;
    private UUID productId;

    @Before
    public void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    @Test
    public void testCreateCart() {
        Cart expectedCart = new Cart(userId, new HashMap<>());
        when(cartRepository.save(any(Cart.class))).thenReturn(expectedCart);

        Cart actualCart = cartService.createCart(userId);

        verify(cartRepository, times(1)).save(any(Cart.class));
        Assert.assertEquals(expectedCart, actualCart);
    }

    @Test
    public void testAddToCart() {
        Map<UUID, Integer> items = new HashMap<>();
        items.put(productId, 1);
        Cart existingCart = new Cart(userId, items);
        when(cartRepository.findByUserId(userId)).thenReturn(existingCart);
        when(cartRepository.save(any(Cart.class))).thenReturn(existingCart);

        Cart expectedCart = new Cart(userId, items);

        Cart actualCart = cartService.addToCart(userId, productId, 1);

        verify(cartRepository, times(1)).findByUserId(userId);
        verify(cartRepository, times(1)).save(any(Cart.class));
        Assert.assertEquals(expectedCart, actualCart);
    }

    @Test
    public void testUpdateCart() throws CartNotFoundException, ItemNotInCartException {
        int quantity = 5;
        Map<UUID, Integer> items = new HashMap<>();
        items.put(productId, 1);
        Cart existingCart = new Cart(userId, items);
        when(cartRepository.findByUserId(userId)).thenReturn(existingCart);

        Cart expectedCart = new Cart(userId, items);
        expectedCart.getItems().put(productId, quantity);

        Cart actualCart = cartService.updateCart(userId, productId, quantity);

        verify(cartRepository, times(1)).findByUserId(userId);
        Assert.assertEquals(expectedCart, actualCart);
    }

    @Test
    public void testClearCartNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(null);

        assertThrows(CartNotFoundException.class, () -> cartService.clearCart(userId));

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testUpdateCartItemNotFound() {
        int quantity = 5;
        Map<UUID, Integer> items = new HashMap<>();
        Cart existingCart = new Cart(userId, items);
        when(cartRepository.findByUserId(userId)).thenReturn(existingCart);

        assertThrows(ItemNotInCartException.class, () -> cartService.updateCart(userId, productId, quantity));

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testGetCartNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(null);

        assertThrows(CartNotFoundException.class, () -> cartService.getCart(userId));

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testGetCart() throws CartNotFoundException {
        Map<UUID, Integer> items = new HashMap<>();
        items.put(productId, 1);
        Cart expectedCart = new Cart(userId, items);
        when(cartRepository.findByUserId(userId)).thenReturn(expectedCart);

        Cart actualCart = cartService.getCart(userId);

        verify(cartRepository, times(1)).findByUserId(userId);
        Assert.assertEquals(expectedCart, actualCart);
    }
}