package com.example.login.service;

import com.example.login.dto.ResponseDTO;

public interface CategoryService {
    ResponseDTO getCategories();
    ResponseDTO getProductsByCategory(Long categoryId);
}