package com.example.login.controller;

import com.example.login.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sms")
public class SmsController {
    
    @Autowired
    private SmsService smsService;
    
    /**
     * 发送短信验证码
     * @param request 请求参数，包含phoneNumber字段
     * @return 发送结果
     */
    @PostMapping("/send-code")
    public Object sendSmsVerifyCode(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        return smsService.sendSmsVerifyCode(phoneNumber);
    }
    
    /**
     * 验证短信验证码
     * @param request 请求参数，包含phoneNumber和code字段
     * @return 验证结果
     */
    @PostMapping("/verify-code")
    public Object verifySmsCode(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String code = request.get("code");
        return smsService.verifySmsCode(phoneNumber, code);
    }
}
