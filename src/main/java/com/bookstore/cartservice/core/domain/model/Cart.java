package com.bookstore.cartservice.core.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "carts")
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @Column(nullable = false)
    @Getter
    private Long userId;

    @Column(name="quantity", nullable = false)
    @Getter
    @Setter
    @ElementCollection
    @MapKeyColumn(name="itemId")
    private Map<UUID, Integer> items;

}