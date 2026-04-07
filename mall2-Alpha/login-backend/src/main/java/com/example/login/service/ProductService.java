package com.example.login.service;

import com.example.login.entity.Product;
import com.example.login.dto.ResponseDTO;

import com.example.login.dto.ResponseDTO;

public interface ProductService {
    ResponseDTO getProducts();
    ResponseDTO getProductById(Long id);
    ResponseDTO searchProducts(String keyword);
    ResponseDTO getHotProducts();
    ResponseDTO removeDuplicates();
}