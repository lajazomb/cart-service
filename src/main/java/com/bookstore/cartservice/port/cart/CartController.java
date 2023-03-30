package com.bookstore.cartservice.port.cart;

import com.bookstore.authentication.DtoUserId;
import com.bookstore.cartservice.core.domain.model.Cart;
import com.bookstore.cartservice.core.domain.service.interfaces.ICartService;
import com.bookstore.cartservice.port.cart.dto.AddToCartRequest;
import com.bookstore.cartservice.port.cart.dto.RemoveFromCartRequest;
import com.bookstore.cartservice.port.cart.dto.UpdateCartRequest;
import com.bookstore.cartservice.port.cart.dto.UserDto;
import com.bookstore.cartservice.port.cart.exception.*;
import com.google.gson.Gson;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@RequestMapping("/api/v1/")
@CrossOrigin(origins = "http://localhost:3000/", allowedHeaders = "*", maxAge = 86400)
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
    public ResponseEntity<Cart> createCart(@RequestHeader("Authorization") String token, @RequestBody UserDto userDto) throws ErrorCreatingCartException, NotAuthorizedException {
        performUUIDCheck(token, userDto.getUserId());

        Cart cart = cartService.createCart(userDto.getUserId());

        if (cart == null) {
            throw new ErrorCreatingCartException();
        }
        return ResponseEntity.ok(cart);
    }

    @GetMapping("cart/user/{userid}")
    public ResponseEntity<Cart> getCart(@RequestHeader("Authorization") String token, @PathVariable UUID userid) throws CartNotFoundException, NotAuthorizedException {
        performUUIDCheck(token, userid);

        return ResponseEntity.ok(cartService.getCart(userid));
    }

    @DeleteMapping("cart/user/{userid}")
    public ResponseEntity<UUID> clearCart(@RequestHeader("Authorization") String token, @PathVariable UUID userid) throws CartNotFoundException, NotAuthorizedException {
        performUUIDCheck(token, userid);
        if (cartService.getCart(userid) != null) {
            cartService.clearCart(userid);
            return ResponseEntity.ok(userid);
        }
        throw new CartNotFoundException();
    }

    @PostMapping("cart")
    public ResponseEntity<Cart> addToCart(@RequestHeader("Authorization") String token, @RequestBody AddToCartRequest addToCartRequest) throws ErrorAddingToCartException, ProductOutOfStockException, NotAuthorizedException {
        System.out.println(addToCartRequest);
        performUUIDCheck(token, UUID.fromString(addToCartRequest.getUserId()));

        StockCheckMessage msg = StockCheckMessage.builder()
                .productId(UUID.fromString(addToCartRequest.getProductId()))
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
            return ResponseEntity.ok(cartService.addToCart(UUID.fromString(addToCartRequest.getUserId()), UUID.fromString(addToCartRequest.getProductId()), addToCartRequest.getQuantity()));        }

        throw new ProductOutOfStockException();
    }

    @PutMapping("cart/update")
    public ResponseEntity<Cart> updateCart(@RequestHeader("Authorization") String token, @RequestBody UpdateCartRequest updateCartRequest) throws ItemNotInCartException, CartNotFoundException, NotAuthorizedException {
        performUUIDCheck(token, updateCartRequest.getUserId());

        return ResponseEntity.ok(cartService.updateCart(updateCartRequest.getUserId(), updateCartRequest.getProductId(), updateCartRequest.getQuantity()));
    }

    @PutMapping("/cart/delete")
    public Cart removeFromCart(@RequestHeader("Authorization") String token, @RequestBody RemoveFromCartRequest removeFromCartRequest) throws ItemNotInCartException, CartNotFoundException, NotAuthorizedException {
        performUUIDCheck(token, removeFromCartRequest.getUserId());
        return cartService.updateCart(removeFromCartRequest.getUserId(), removeFromCartRequest.getProductId(), 0);
    }

    private void performUUIDCheck(String token, UUID uuid) throws NotAuthorizedException {
        token = token.substring(7);

        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        Gson g = new Gson();
        DtoUserId req = g.fromJson(payload, DtoUserId.class);

        boolean res = req.userid.equals(uuid);

        if (!res) throw new NotAuthorizedException();
    }
}
