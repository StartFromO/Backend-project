package com.example.login.service.impl;

import com.example.login.entity.Cart;
import com.example.login.entity.Product;
import com.example.login.repository.CartRepository;
import com.example.login.repository.ProductRepository;
import com.example.login.service.CartService;
import com.example.login.service.CacheService;
import com.example.login.dto.ResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseDTO addToCart(String username, Long productId, int quantity) {
        // 检查商品是否存在
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseDTO.error(404, "商品不存在");
        }

        // 检查库存
        if (product.getStock() < quantity) {
            return ResponseDTO.error(400, "库存不足");
        }

        // 检查购物车中是否已存在该商品
        List<Cart> cartItems = cartRepository.findByUsername(username);
        Cart existingCartItem = cartItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingCartItem != null) {
            // 更新数量
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartRepository.save(existingCartItem);
        } else {
            // 添加新商品
            Cart cart = new Cart();
            cart.setUsername(username);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cartRepository.save(cart);
        }

        // 清除购物车缓存
        String cacheKey = "cart:" + username;
        cacheService.delete(cacheKey);

        return ResponseDTO.success(null);
    }

    @Override
    public ResponseDTO getCart(String username) {
        // 尝试从缓存中获取购物车数据
        String cacheKey = "cart:" + username;
        List<CartWithProduct> cartWithProducts = (List<CartWithProduct>) cacheService.get(cacheKey);
        
        if (cartWithProducts == null) {
            // 缓存中没有，从数据库中查询
            List<Cart> cartItems = cartRepository.findByUsername(username);
            // 关联商品信息
            cartWithProducts = cartItems.stream()
                    .map(item -> {
                        Product product = productRepository.findById(item.getProductId()).orElse(null);
                        return new CartWithProduct(item, product);
                    })
                    .collect(Collectors.toList());
            // 将结果存入缓存，设置过期时间为30分钟
            cacheService.set(cacheKey, cartWithProducts, 1800);
        }
        
        return ResponseDTO.success(cartWithProducts);
    }

    @Override
    public ResponseDTO updateCart(String username, Long productId, int quantity) {
        if (quantity <= 0) {
            return ResponseDTO.error(400, "数量必须大于0");
        }

        // 检查商品是否存在
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseDTO.error(404, "商品不存在");
        }

        // 检查库存
        if (product.getStock() < quantity) {
            return ResponseDTO.error(400, "库存不足");
        }

        // 查找购物车项
        List<Cart> cartItems = cartRepository.findByUsername(username);
        Cart cartItem = cartItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (cartItem == null) {
            return ResponseDTO.error(404, "购物车中不存在该商品");
        }

        // 更新数量
        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
        
        // 清除购物车缓存
        String cacheKey = "cart:" + username;
        cacheService.delete(cacheKey);
        
        return ResponseDTO.success(null);
    }

    @Modifying
    @Transactional
    @Override
    public ResponseDTO removeFromCart(String username, Long productId) {
        cartRepository.deleteByUsernameAndProductId(username, productId);
        
        // 清除购物车缓存
        String cacheKey = "cart:" + username;
        cacheService.delete(cacheKey);
        
        return ResponseDTO.success(null);
    }

    // 内部类，用于返回购物车项和商品信息
    private static class CartWithProduct {
        private Cart cart;
        private Product product;

        public CartWithProduct(Cart cart, Product product) {
            this.cart = cart;
            this.product = product;
        }

        public Cart getCart() {
            return cart;
        }

        public void setCart(Cart cart) {
            this.cart = cart;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }
    }
}