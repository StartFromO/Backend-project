package com.example.login.service;

import com.example.login.dto.ResponseDTO;

public interface OrderService {
    ResponseDTO createOrder(String username, String address);
    ResponseDTO getOrderList(String username, String status);
    ResponseDTO getOrderDetail(Long orderId);
    ResponseDTO confirmReceipt(Long orderId);
    ResponseDTO viewLogistics(Long orderId);
    ResponseDTO cancelOrder(Long orderId);
    ResponseDTO refundOrder(Long orderId, String reason);
}