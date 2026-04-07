package com.example.login.service.impl;

import com.example.login.entity.*;
import com.example.login.repository.*;
import com.example.login.service.OrderService;
import com.example.login.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public ResponseDTO createOrder(String username, String address) {
        // 获取用户的购物车物品
        List<Cart> cartItems = cartRepository.findByUsername(username);
        if (cartItems.isEmpty()) {
            return ResponseDTO.error(400, "购物车为空");
        }
        
        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : cartItems) {
            Product product = productRepository.findById(cart.getProductId()).orElse(null);
            if (product == null) {
                return ResponseDTO.error(404, "商品不存在");
            }
            if (product.getStock() < cart.getQuantity()) {
                return ResponseDTO.error(400, "商品库存不足: " + product.getName());
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }
        
        // 生成25位订单编号
        String orderNumber = generateOrderNumber(username);
        
        // 创建订单
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUsername(username);
        order.setTotalAmount(totalAmount);
        order.setStatus("待付款");
        order.setPaymentStatus("未支付");
        order.setReceiptStatus("未收货");
        order.setUserAddress(address);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
        orderRepository.save(order);
        
        // 创建订单项
        for (Cart cart : cartItems) {
            Product product = productRepository.findById(cart.getProductId()).orElse(null);
            if (product != null) {
                // 创建订单项
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName());
                orderItem.setPrice(product.getPrice());
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
                orderItemRepository.save(orderItem);
                
                // 清空购物车
                cartRepository.delete(cart);
            }
        }
        
        return ResponseDTO.success("订单创建成功");
    }

    @Override
    public ResponseDTO getOrderList(String username, String status) {
        List<Order> orders = orderRepository.findByUsername(username);
        
        // 根据状态筛选
        if (status != null && !status.isEmpty() && !"全部".equals(status)) {
            orders = orders.stream()
                    .filter(order -> status.equals(order.getStatus()))
                    .collect(Collectors.toList());
        }
        
        return ResponseDTO.success(orders);
    }

    @Override
    public ResponseDTO getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseDTO.error(404, "订单不存在");
        }
        
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        
        // 构建订单详情
        return ResponseDTO.success(order);
    }

    @Override
    @Transactional
    public ResponseDTO confirmReceipt(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseDTO.error(404, "订单不存在");
        }
        
        if (!"待收货".equals(order.getStatus())) {
            return ResponseDTO.error(400, "订单状态不是待收货");
        }
        
        order.setStatus("已完成");
        order.setReceiptStatus("已收货");
        order.setUpdatedAt(new Date());
        orderRepository.save(order);
        
        return ResponseDTO.success("确认收货成功");
    }

    @Override
    public ResponseDTO viewLogistics(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseDTO.error(404, "订单不存在");
        }
        
        if (order.getLogisticsInfo() == null) {
            return ResponseDTO.error(400, "暂无物流信息");
        }
        
        return ResponseDTO.success(order.getLogisticsInfo());
    }

    @Override
    @Transactional
    public ResponseDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseDTO.error(404, "订单不存在");
        }
        
        if ("已完成".equals(order.getStatus())) {
            return ResponseDTO.error(400, "已完成的订单无法取消");
        }
        
        order.setStatus("已取消");
        order.setUpdatedAt(new Date());
        orderRepository.save(order);
        
        return ResponseDTO.success("订单取消成功");
    }

    @Override
    @Transactional
    public ResponseDTO refundOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseDTO.error(404, "订单不存在");
        }
        
        if (!"已完成".equals(order.getStatus())) {
            return ResponseDTO.error(400, "只有已完成的订单才能退款");
        }
        
        order.setStatus("已退款");
        order.setUpdatedAt(new Date());
        orderRepository.save(order);
        
        return ResponseDTO.success("退款申请成功");
    }
    
    // 生成25位订单编号
    private String generateOrderNumber(String username) {
        // 格式：年月日时分秒 + 用户名哈希值 + 随机数
        Date now = new Date();
        String timestamp = String.format("%tY%<tm%<td%<tH%<tM%<tS", now);
        int usernameHash = Math.abs(username.hashCode());
        int random = (int) (Math.random() * 10000);
        String orderNumber = timestamp + String.format("%08d", usernameHash % 100000000) + String.format("%04d", random);
        // 确保订单编号为25位
        if (orderNumber.length() > 25) {
            orderNumber = orderNumber.substring(0, 25);
        } else if (orderNumber.length() < 25) {
            orderNumber = orderNumber + String.format("%0" + (25 - orderNumber.length()) + "d", 0);
        }
        return orderNumber;
    }
}