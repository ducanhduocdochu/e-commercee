package com.example.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cart.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByBuyerId(String buyerId);
}
