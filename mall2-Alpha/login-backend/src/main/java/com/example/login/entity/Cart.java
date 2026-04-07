package com.example.login.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private int quantity;
    
    private boolean checked = true;
}