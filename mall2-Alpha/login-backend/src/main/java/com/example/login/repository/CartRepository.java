package com.example.login.repository;

import com.example.login.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUsername(String username);
    void deleteByUsernameAndProductId(String username, Long productId);
}