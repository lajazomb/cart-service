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
    private UUID userId;

    @Column(name="quantity", nullable = false)
    @Getter
    @Setter
    @ElementCollection
    @MapKeyColumn(name="itemId")
    private Map<UUID, Integer> items;

    @Override
    public boolean equals(Object obj) {
        return userId.equals(((Cart) obj).userId) && items.equals(((Cart) obj).items);
    }
}