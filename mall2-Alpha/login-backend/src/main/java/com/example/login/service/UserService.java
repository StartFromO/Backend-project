package com.example.login.service;

import com.example.login.dto.LoginRequest;
import com.example.login.dto.RegisterRequest;
import com.example.login.dto.ForgotPasswordRequest;
import com.example.login.dto.ResponseDTO;

public interface UserService {
    ResponseDTO login(LoginRequest loginRequest);
    ResponseDTO register(RegisterRequest registerRequest);
    ResponseDTO sendCode(String phone);
    ResponseDTO forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
}