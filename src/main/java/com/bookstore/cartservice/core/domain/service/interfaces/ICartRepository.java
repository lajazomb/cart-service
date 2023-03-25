package com.bookstore.cartservice.core.domain.service.interfaces;

import com.bookstore.cartservice.core.domain.model.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ICartRepository extends CrudRepository<Cart, String> {
    Cart findByUserId(UUID userId);
}
