package com.mall.service;

import com.mall.dto.PageResult;
import com.mall.dto.ProductDTO;
import com.mall.entity.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品服务接口
 */
@Service
public interface ProductService {

    /**
     * 添加商品
     */
    Product addProduct(ProductDTO productDTO);

    /**
     * 更新商品
     */
    Product updateProduct(Long id, ProductDTO productDTO);

    /**
     * 删除商品
     */
    void deleteProduct(Long id);

    /**
     * 根据ID查询商品
     */
    Product getProductById(Long id);

    /**
     * 分页查询所有商品
     */
    PageResult<Product> getAllProducts(Integer pageNum, Integer pageSize);

    /**
     * 搜索商品
     */
    PageResult<Product> searchProducts(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 高级搜索
     */
    PageResult<Product> advancedSearch(String keyword, String category, 
                                        BigDecimal minPrice, BigDecimal maxPrice, 
                                        Integer pageNum, Integer pageSize);

    /**
     * 获取热门推荐商品
     */
    List<Product> getHotProducts();

    /**
     * 分页获取热门推荐商品
     */
    PageResult<Product> getHotProductsPage(Integer pageNum, Integer pageSize);

    /**
     * 根据分类查询商品
     */
    PageResult<Product> getProductsByCategory(String category, Integer pageNum, Integer pageSize);

    /**
     * 更新商品热门状态
     */
    Product updateHotStatus(Long id, Boolean isHot);


    Product addSales(Long id, Integer quantity);
}
