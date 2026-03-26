package com.mall.repository;

import com.mall.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品数据访问层
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 根据名称模糊查询z
     */
    Page<Product> findByNameContaining(String name, Pageable pageable);

    /**
     * 根据分类查询
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * 根据名称或分类模糊查询
     */
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.category LIKE %:keyword%")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询热门商品
     */
    List<Product> findByIsHotTrueAndStatusOrderBySalesDesc(Integer status);

    /**
     * 查询热门商品（分页）
     */
    Page<Product> findByIsHotTrueAndStatus(Integer status, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<Product> findByStatus(Integer status, Pageable pageable);

    /**
     * 查询销量最高的商品
     */
    List<Product> findTop10ByStatusOrderBySalesDesc(Integer status);

    /**
     * 综合搜索：根据关键字、分类、价格范围查询
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "p.status = :status")
    Page<Product> advancedSearch(@Param("keyword") String keyword,
                                  @Param("category") String category,
                                  @Param("minPrice") java.math.BigDecimal minPrice,
                                  @Param("maxPrice") java.math.BigDecimal maxPrice,
                                  @Param("status") Integer status,
                                  Pageable pageable);
}
