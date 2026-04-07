package com.example.login.service.impl;

import com.example.login.entity.Category;
import com.example.login.entity.Product;
import com.example.login.repository.CategoryRepository;
import com.example.login.repository.ProductRepository;
import com.example.login.service.CategoryService;
import com.example.login.service.CacheService;
import com.example.login.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseDTO getCategories() {
        // 尝试从缓存中获取分类列表
        String cacheKey = "categories:list";
        List<Category> categories = (List<Category>) cacheService.get(cacheKey);
        
        if (categories == null) {
            // 缓存中没有，从数据库中查询
            categories = categoryRepository.findAll();
            // 将结果存入缓存，设置过期时间为10分钟
            cacheService.set(cacheKey, categories, 600);
        }
        
        return ResponseDTO.success(categories);
    }

    @Override
    public ResponseDTO getProductsByCategory(Long categoryId) {
        // 尝试从缓存中获取分类商品
        String cacheKey = "categories:products:" + categoryId;
        List<Product> filteredProducts = (List<Product>) cacheService.get(cacheKey);
        
        if (filteredProducts == null) {
            // 根据分类ID获取分类名称
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null) {
                return ResponseDTO.error(404, "分类不存在");
            }
            
            // 根据分类名称获取对应的categoryId值
            Long targetCategoryId = getCategoryIdByCategoryName(category.getName());
            
            List<Product> products = productRepository.findAll();
            filteredProducts = products.stream()
                    .filter(product -> product.getCategoryId() != null && targetCategoryId.equals(product.getCategoryId()))
                    .collect(Collectors.toList());
            
            // 将结果存入缓存，设置过期时间为10分钟
            cacheService.set(cacheKey, filteredProducts, 600);
        }
        
        return ResponseDTO.success(filteredProducts);
    }
    
    // 根据分类名称获取对应的categoryId值
    private Long getCategoryIdByCategoryName(String categoryName) {
        switch (categoryName) {
            case "手机": return 1L;
            case "电脑": return 2L;
            case "耳机": return 3L;
            case "平板": return 4L;
            case "手表": return 5L;
            case "游戏机": return 6L;
            case "相机": return 7L;
            case "智能家居": return 8L;
            default: return null;
        }
    }
}