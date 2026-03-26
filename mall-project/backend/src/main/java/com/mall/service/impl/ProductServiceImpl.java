package com.mall.service.impl;

import com.mall.dto.PageResult;
import com.mall.dto.ProductDTO;
import com.mall.entity.Product;
import com.mall.mapper.ProductMapper;
import com.mall.repository.ProductRepository;
import com.mall.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品服务实现类
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    /**
     * 增加商品
     */
    @Override
    @Transactional
    public Product addProduct(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        product.setSales(0);
        return productRepository.save(product);
    }

    /**
     * 更新商品
     */
    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + id));
        
        BeanUtils.copyProperties(productDTO, product, "id", "sales", "createTime");
        return productRepository.save(product);
    }

    /**
     * 删除商品
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("商品不存在: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * 根据ID获得商品
     */
    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + id));
    }

    /**
     * 获得所有商品
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<Product> getAllProducts(Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, 
                Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Product> page = productRepository.findAll(pageable);
        return new PageResult<>(pageNum, pageSize, page.getTotalElements(), page.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Product> searchProducts(String keyword, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, 
                Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Product> page = productRepository.searchByKeyword(keyword, pageable);
        return new PageResult<>(pageNum, pageSize, page.getTotalElements(), page.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Product> advancedSearch(String keyword, String category, 
                                               BigDecimal minPrice, BigDecimal maxPrice, 
                                               Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, 
                Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Product> page = productRepository.advancedSearch(
                keyword, category, minPrice, maxPrice, 1, pageable);
        return new PageResult<>(pageNum, pageSize, page.getTotalElements(), page.getContent());
    }

    /**
     * 获取热门商品
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getHotProducts() {
        return productRepository.findByIsHotTrueAndStatusOrderBySalesDesc(1);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Product> getHotProductsPage(Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, 
                Sort.by(Sort.Direction.DESC, "sales"));
        Page<Product> page = productRepository.findByIsHotTrueAndStatus(1, pageable);
        return new PageResult<>(pageNum, pageSize, page.getTotalElements(), page.getContent());
    }

    /**
     * 根据种类进行商品分类
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<Product> getProductsByCategory(String category, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, 
                Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Product> page = productRepository.findByCategory(category, pageable);
        return new PageResult<>(pageNum, pageSize, page.getTotalElements(), page.getContent());
    }

    /**
     * 更新热门商品
     */
    @Override
    @Transactional
    public Product updateHotStatus(Long id, Boolean isHot) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + id));
        product.setIsHot(isHot);
        return productRepository.save(product);
    }

    @Override
    public Product addSales(Long id, Integer quantity) {
        return null;
    }

}

