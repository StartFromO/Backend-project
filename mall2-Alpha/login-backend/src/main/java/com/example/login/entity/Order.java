package com.example.login.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 30)
    private String orderNumber; // 订单编号，25位数字
    
    @Column(nullable = false)
    private String username;
    
    private BigDecimal totalAmount;
    
    private String status; // 订单状态：待付款、待发货、待收货、已完成、已取消
    
    private String paymentStatus; // 支付状态：未支付、已支付
    
    private String receiptStatus; // 收货状态：未收货、已收货
    
    @Column(name = "express_company")
    private String expressCompany; // 快递公司
    
    @Column(name = "express_number")
    private String expressNumber; // 快递单号
    
    @Column(name = "user_address", length = 500)
    private String userAddress; // 用户地址
    
    @Column(name = "logistics_info", length = 1000)
    private String logisticsInfo; // 物流信息
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_at")
    private Date updatedAt;
}