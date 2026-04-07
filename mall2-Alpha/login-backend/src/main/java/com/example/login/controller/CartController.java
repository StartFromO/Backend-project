package com.example.login.controller;

import com.example.login.service.CartService;
import com.example.login.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseDTO addToCart(@RequestParam String username, @RequestParam Long productId, @RequestParam int quantity) {
        return cartService.addToCart(username, productId, quantity);
    }

    @GetMapping("/list")
    public ResponseDTO getCart(@RequestParam String username) {
        return cartService.getCart(username);
    }

    @PutMapping("/update")
    public ResponseDTO updateCart(@RequestParam String username, @RequestParam Long productId, @RequestParam int quantity) {
        return cartService.updateCart(username, productId, quantity);
    }

    @DeleteMapping("/remove")
    public ResponseDTO removeFromCart(@RequestParam String username, @RequestParam Long productId) {
        return cartService.removeFromCart(username, productId);
    }
}