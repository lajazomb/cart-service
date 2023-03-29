package com.bookstore.cartservice.port.cart;

import com.bookstore.cartservice.core.domain.model.Cart;
import com.bookstore.cartservice.core.domain.service.interfaces.ICartService;
import com.bookstore.cartservice.port.cart.dto.UserDto;
import com.bookstore.cartservice.port.cart.exception.CartNotFoundException;
import com.bookstore.cartservice.port.cart.exception.ItemNotInCartException;
import com.bookstore.cartservice.port.cart.exception.NotAuthorizedException;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTests {

    @Mock
    private ICartService cartService;

    @InjectMocks
    private CartController cartController;

    private MockMvc mockMvc;

    private UUID userId;
    private UUID productId;
    private int quantity;
    private String token;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        quantity = 5;

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userid", userId);

        token = "Bearer " + Jwts.builder()
                .setSubject(userId.toString())
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor("3979244226452948404D6351665468576D5A7134743777217A25432A462D4A614E645267556B586E3272357538782F413F4428472B4B6250655368566D5971337336763979244226452948404D635166546A576E5A7234753777217A25432A462D4A614E645267556B58703273357638792F413F4428472B4B6250655368566D".getBytes()))
                .compact();
    }

    @Test
    public void testCreateCart() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);

        Cart expectedCart = new Cart(userId, new HashMap<>());
        Mockito.when(cartService.createCart(userId)).thenReturn(expectedCart);

        mockMvc.perform(post("/api/v1/cart/create")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(expectedCart)));
    }

    @Test
    public void testCreateCartNotAuthorized() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(UUID.randomUUID());

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/api/v1/cart/create")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new Gson().toJson(userDto)))
                    .andExpect(status().isUnauthorized());
        });
    }

    @Test
    public void testCreateCartError() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);

        Mockito.when(cartService.createCart(userId)).thenReturn(null);

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/api/v1/cart/create")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new Gson().toJson(userDto)))
                    .andExpect(status().isInternalServerError());
        });
    }

    @Test
    public void testGetCart() throws Exception {
        Cart expectedCart = new Cart(userId, new HashMap<>());
        Mockito.when(cartService.getCart(userId)).thenReturn(expectedCart);

        mockMvc.perform(get("/api/v1/cart/user/{userid}", userId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(expectedCart)));
    }

    @Test
    public void testGetCartNotAuthorized() {
        // The ServletException is caused by the NotAuthorizedException
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(get("/api/v1/cart/user/{userid}", UUID.randomUUID())
                            .header("Authorization", token))
                    .andExpect(status().isUnauthorized());
        });
    }

    @Test
    public void testGetCartNotFound() throws Exception {
        Mockito.when(cartService.getCart(userId)).thenThrow(new CartNotFoundException());

        // The ServletException is caused by the CartNotFoundException
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(get("/api/v1/cart/user/{userid}", userId)
                            .header("Authorization", token))
                    .andExpect(status().isNotFound());
        });

    }

    @Test
    public void testClearCart() throws Exception {
        mockMvc.perform(delete("/api/v1/cart/user/{userid}", userId)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }
}
