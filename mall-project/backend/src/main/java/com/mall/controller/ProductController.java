package com.mall.controller;

import com.mall.dto.PageResult;
import com.mall.dto.ProductDTO;
import com.mall.dto.Result;
import com.mall.entity.Product;
import com.mall.service.ProductService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductController {

    @Resource
    private final ProductService productService;

    /**
     * 添加商品
     */
    @PostMapping
    public Result<Product> addProduct(@Valid @RequestBody ProductDTO productDTO) {
        log.info("添加商品: {}", productDTO.getName());
        Product product = productService.addProduct(productDTO);
        return Result.success("商品添加成功", product);
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result<Product> updateProduct(@PathVariable Long id, 
                                          @Valid @RequestBody ProductDTO productDTO) {
        log.info("更新商品: {}", id);
        Product product = productService.updateProduct(id, productDTO);
        return Result.success("商品更新成功", product);
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        log.info("删除商品: {}", id);
        productService.deleteProduct(id);
        return Result.success("商品删除成功", null);
    }

    /**
     * 根据ID查询商品
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return Result.success(product);
    }

    /**
     * 分页查询所有商品
     */
    @GetMapping
    public Result<PageResult<Product>> getAllProducts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Product> result = productService.getAllProducts(pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public Result<PageResult<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("搜索商品: keyword={}", keyword);
        PageResult<Product> result = productService.searchProducts(keyword, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 高级搜索
     */
    @GetMapping("/advanced-search")
    public Result<PageResult<Product>> advancedSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("高级搜索: keyword={}, category={}, minPrice={}, maxPrice={}", 
                keyword, category, minPrice, maxPrice);
        PageResult<Product> result = productService.advancedSearch(
                keyword, category, minPrice, maxPrice, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 获取热门推荐商品
     */
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts() {
        List<Product> products = productService.getHotProducts();
        return Result.success(products);
    }

    /**
     * 分页获取热门推荐商品
     */
    @GetMapping("/hot/page")
    public Result<PageResult<Product>> getHotProductsPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Product> result = productService.getHotProductsPage(pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 根据分类查询商品
     */
    @GetMapping("/category/{category}")
    public Result<PageResult<Product>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Product> result = productService.getProductsByCategory(category, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 设置商品热门状态
     */
    @PutMapping("/{id}/hot")
    public Result<Product> updateHotStatus(@PathVariable Long id, 
                                            @RequestParam Boolean isHot) {
        log.info("设置商品热门状态: id={}, isHot={}", id, isHot);
        Product product = productService.updateHotStatus(id, isHot);
        return Result.success("热门状态更新成功", product);
    }

    /**
     * 增加商品销量
     */
    @PostMapping("/{id}/sales")
    public Result<Void> addSales(@PathVariable Long id,
                                       @RequestParam(defaultValue = "1") Integer quantity) {
        log.info("增加商品销量: id={}, quantity={}", id, quantity);
        productService.addSales(id, quantity);
        return Result.success("销量更新成功", null);
    }
}
