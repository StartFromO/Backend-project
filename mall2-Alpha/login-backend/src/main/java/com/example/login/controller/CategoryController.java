package com.example.login.controller;

import com.example.login.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Object getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/products/{categoryId}")
    public Object getProductsByCategory(@PathVariable Long categoryId) {
        return categoryService.getProductsByCategory(categoryId);
    }
}