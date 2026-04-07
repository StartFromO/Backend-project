package com.example.login.controller;

import com.example.login.service.ProductService;
import com.example.login.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public ResponseDTO getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/detail/{id}")
    public ResponseDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/search")
    public ResponseDTO searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    @GetMapping("/hot")
    public ResponseDTO getHotProducts() {
        return productService.getHotProducts();
    }
    
    @DeleteMapping("/remove-duplicates")
    public ResponseDTO removeDuplicates() {
        return productService.removeDuplicates();
    }
}