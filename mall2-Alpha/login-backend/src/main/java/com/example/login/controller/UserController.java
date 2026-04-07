package com.example.login.controller;

import com.example.login.dto.LoginRequest;
import com.example.login.dto.RegisterRequest;
import com.example.login.dto.ForgotPasswordRequest;
import com.example.login.dto.ResponseDTO;
import com.example.login.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseDTO login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseDTO register(@Valid @RequestBody RegisterRequest registerRequest) {
        return userService.register(registerRequest);
    }

    @PostMapping("/send-code")
    public ResponseDTO sendCode(@RequestParam String phone) {
        return userService.sendCode(phone);
    }

    @PostMapping("/forgot-password")
    public ResponseDTO forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return userService.forgotPassword(forgotPasswordRequest);
    }
}