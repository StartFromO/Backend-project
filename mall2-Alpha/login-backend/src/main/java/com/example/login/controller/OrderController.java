package com.example.login.controller;

import com.example.login.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Object createOrder(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String address = request.get("address");
        return orderService.createOrder(username, address);
    }

    @GetMapping("/list")
    public Object getOrderList(@RequestParam String username, @RequestParam(required = false) String status) {
        return orderService.getOrderList(username, status);
    }

    @GetMapping("/detail/{orderId}")
    public Object getOrderDetail(@PathVariable Long orderId) {
        return orderService.getOrderDetail(orderId);
    }

    @PostMapping("/confirm-receipt/{orderId}")
    public Object confirmReceipt(@PathVariable Long orderId) {
        return orderService.confirmReceipt(orderId);
    }

    @GetMapping("/logistics/{orderId}")
    public Object viewLogistics(@PathVariable Long orderId) {
        return orderService.viewLogistics(orderId);
    }

    @PostMapping("/cancel/{orderId}")
    public Object cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }

    @PostMapping("/refund/{orderId}")
    public Object refundOrder(@PathVariable Long orderId, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        return orderService.refundOrder(orderId, reason);
    }
}