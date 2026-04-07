package com.example.login.service.impl;

import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import darabonba.core.client.ClientOverrideConfiguration;
import com.example.login.service.SmsService;
import com.example.login.service.CacheService;
import com.example.login.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SmsServiceImpl implements SmsService {
    
    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseDTO sendSmsVerifyCode(String phoneNumber) {
        try {
            // 生成6位随机验证码
            String code = String.format("%06d", (int) (Math.random() * 1000000));
            
            // 配置阿里云短信验证API
            StaticCredentialProvider provider = StaticCredentialProvider.create(
                    Credential.builder()
                            .accessKeyId("") //在阿里云开通短信验证服务并注册ram获取AccessKEY
                            .accessKeySecret("")
                            .build()
            );
            
            try (AsyncClient client = AsyncClient.builder()
                    .region("ap-southeast-1")
                    .credentialsProvider(provider)
                    .overrideConfiguration(
                            ClientOverrideConfiguration.create()
                                    .setEndpointOverride("dypnsapi.aliyuncs.com")
                    )
                    .build()) {
                
                // 构建发送短信验证码请求
                SendSmsVerifyCodeRequest request = SendSmsVerifyCodeRequest.builder()
                        .signName("速通互联验证码")
                        .templateCode("100001")
                        .phoneNumber(phoneNumber)
                        .templateParam(String.format("{\"code\":\"%s\",\"min\":\"5\"}", code))
                        .countryCode("86")
                        .schemeName("商城系统测试方案")
                        .codeLength(6L)
                        .validTime(300L)
                        .duplicatePolicy(1L)
                        .interval(60L)
                        .codeType(1L)
                        .returnVerifyCode(true)
                        .build();
                
                // 发送短信验证码
                SendSmsVerifyCodeResponse response = client.sendSmsVerifyCode(request).get();
                
                // 将验证码存储到Redis，有效期5分钟
                String cacheKey = "sms:verify:" + phoneNumber;
                cacheService.set(cacheKey, code, 300);
                
                return ResponseDTO.success("短信验证码发送成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.error(500, "短信验证码发送失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseDTO verifySmsCode(String phoneNumber, String code) {
        try {
            // 从Redis获取存储的验证码
            String cacheKey = "sms:verify:" + phoneNumber;
            String storedCode = (String) cacheService.get(cacheKey);
            
            if (storedCode == null) {
                return ResponseDTO.error(400, "验证码已过期");
            }
            
            if (!storedCode.equals(code)) {
                return ResponseDTO.error(400, "验证码错误");
            }
            
            // 验证成功后删除验证码
            cacheService.delete(cacheKey);
            
            return ResponseDTO.success("验证码验证成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.error(500, "验证码验证失败: " + e.getMessage());
        }
    }
}
