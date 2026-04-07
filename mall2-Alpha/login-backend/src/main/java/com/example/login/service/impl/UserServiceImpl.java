package com.example.login.service.impl;

import com.example.login.dto.LoginRequest;
import com.example.login.dto.RegisterRequest;
import com.example.login.dto.ForgotPasswordRequest;
import com.example.login.dto.ResponseDTO;
import com.example.login.entity.User;
import com.example.login.repository.UserRepository;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 存储验证码，实际项目中应该使用Redis
    private Map<String, String> codeMap = new HashMap<>();

    @Override
    public ResponseDTO login(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (!userOptional.isPresent()) {
            return ResponseDTO.error(401, "用户名或密码错误");
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseDTO.error(401, "用户名或密码错误");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("phone", user.getPhone());
        return ResponseDTO.success(data);
    }

    @Override
    public ResponseDTO register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseDTO.error(400, "用户名已存在");
        }
        // 检查手机号是否已存在
        if (userRepository.findByPhone(registerRequest.getPhone()).isPresent()) {
            return ResponseDTO.error(400, "手机号已被注册");
        }
        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());
        userRepository.save(user);
        return ResponseDTO.success(null);
    }

    @Override
    public ResponseDTO sendCode(String phone) {
        // 检查手机号是否存在
        if (!userRepository.findByPhone(phone).isPresent()) {
            return ResponseDTO.error(400, "手机号未注册");
        }
        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(999999));
        // 存储验证码，实际项目中应该设置过期时间
        codeMap.put(phone, code);
        // 模拟发送验证码
        System.out.println("向" + phone + "发送验证码：" + code);
        return ResponseDTO.success(null);
    }

    @Override
    public ResponseDTO forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        // 检查手机号是否存在
        Optional<User> userOptional = userRepository.findByPhone(forgotPasswordRequest.getPhone());
        if (!userOptional.isPresent()) {
            return ResponseDTO.error(400, "手机号未注册");
        }
        // 验证验证码
        String code = codeMap.get(forgotPasswordRequest.getPhone());
        if (code == null || !code.equals(forgotPasswordRequest.getCode())) {
            return ResponseDTO.error(400, "验证码错误");
        }
        // 更新密码
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(forgotPasswordRequest.getNewPassword()));
        userRepository.save(user);
        // 清除验证码
        codeMap.remove(forgotPasswordRequest.getPhone());
        return ResponseDTO.success(null);
    }
}