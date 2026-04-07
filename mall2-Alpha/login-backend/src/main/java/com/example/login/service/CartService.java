package com.example.login.service;

import com.example.login.dto.ResponseDTO;

public interface CartService {
    ResponseDTO addToCart(String username, Long productId, int quantity);
    ResponseDTO getCart(String username);
    ResponseDTO updateCart(String username, Long productId, int quantity);
    ResponseDTO removeFromCart(String username, Long productId);
}