package com.mall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商品名称
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * 商品描述
     */
    @Column(length = 2000)
    private String description;

    /**
     * 商品价格
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 商品原价
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;

    /**
     * 商品库存
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * 商品分类
     */
    @Column(length = 50)
    private String category;

    /**
     * 商品图片URL
     */
    @Column(length = 500)
    private String imageUrl;

    /**
     * 是否热门
     */
    @Column(nullable = false)
    private Boolean isHot = false;

    /**
     * 销量
     */
    @Column(nullable = false)
    private Integer sales = 0;

    /**
     * 商品状态：0-下架，1-上架
     */
    @Column(nullable = false)
    private Integer status = 1;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    private LocalDateTime updateTime;
}
