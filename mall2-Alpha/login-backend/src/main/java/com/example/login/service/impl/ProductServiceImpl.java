package com.example.login.service.impl;

import com.example.login.entity.Product;
import com.example.login.repository.ProductRepository;
import com.example.login.service.ProductService;
import com.example.login.service.CacheService;
import com.example.login.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseDTO getProducts() {
        // 尝试从缓存中获取商品列表
        String cacheKey = "products:list";
        List<Product> products = (List<Product>) cacheService.get(cacheKey);
        
        if (products == null) {
            // 缓存中没有，从数据库中查询
            products = productRepository.findAll();
            // 将结果存入缓存，设置过期时间为10分钟
            cacheService.set(cacheKey, products, 600);
        }
        
        return ResponseDTO.success(products);
    }

    @Override
    public ResponseDTO getProductById(Long id) {
        // 尝试从缓存中获取商品详情
        String cacheKey = "products:detail:" + id;
        Product product = (Product) cacheService.get(cacheKey);
        
        if (product == null) {
            // 缓存中没有，从数据库中查询
            product = productRepository.findById(id).orElse(null);
            if (product == null) {
                return ResponseDTO.error(404, "商品不存在");
            }
            // 将结果存入缓存，设置过期时间为10分钟
            cacheService.set(cacheKey, product, 600);
        }
        
        return ResponseDTO.success(product);
    }

    @Override
    public ResponseDTO searchProducts(String keyword) {
        List<Product> products = productRepository.findAll();
        // 简单的关键词搜索，实际项目中应该使用Elasticsearch
        List<Product> filteredProducts = products.stream()
                .filter(product -> product.getName().contains(keyword) || 
                        (product.getDescription() != null && product.getDescription().contains(keyword)))
                .collect(Collectors.toList());
        return ResponseDTO.success(filteredProducts);
    }

    @Override
    public ResponseDTO getHotProducts() {
        List<Product> products = productRepository.findAll();
        List<Product> hotProducts = products.stream()
                .filter(Product::isHot)
                .collect(Collectors.toList());
        return ResponseDTO.success(hotProducts);
    }
    
    @Override
    @Transactional
    public ResponseDTO removeDuplicates() {
        try {
            // 删除重复的商品数据，只保留每个名称的一条记录
            String deleteDuplicatesSQL = "DELETE FROM products WHERE id IN (" +
                                      "SELECT id FROM (" +
                                      "SELECT id, name, ROW_NUMBER() OVER (PARTITION BY name ORDER BY id) AS row_num " +
                                      "FROM products" +
                                      ") AS cte WHERE row_num > 1" +
                                      ")";
            
            int affectedRows = entityManager.createNativeQuery(deleteDuplicatesSQL).executeUpdate();
            
            // 获取删除后的商品数量
            String countSQL = "SELECT COUNT(*) FROM products";
            Long productCount = (Long) entityManager.createNativeQuery(countSQL).getSingleResult();
            
            // 清除商品列表缓存
            cacheService.delete("products:list");
            
            return ResponseDTO.success("删除了 " + affectedRows + " 条重复记录，删除后商品总数: " + productCount);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.error(500, "删除重复数据失败: " + e.getMessage());
        }
    }
}