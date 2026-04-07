package com.example.login.service;

import com.example.login.dto.ResponseDTO;

public interface SmsService {
    ResponseDTO sendSmsVerifyCode(String phoneNumber);
    ResponseDTO verifySmsCode(String phoneNumber, String code);
}
