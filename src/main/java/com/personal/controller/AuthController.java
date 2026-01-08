package com.personal.controller;

import com.personal.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @PostMapping("/wechat/login")
    public Result<String> wechatLogin(@RequestBody WechatLoginRequest request) {
        // TODO: 实现微信登录逻辑
        // 1. 通过code获取openid
        // 2. 生成JWT token
        return Result.success("登录成功");
    }
    
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest request) {
        // TODO: 实现用户注册逻辑
        return Result.success("注册成功");
    }
    
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest request) {
        // TODO: 实现用户登录逻辑
        return Result.success("登录成功");
    }
    
    // 内部类：请求DTO
    public static class WechatLoginRequest {
        private String code;
        // getter/setter
    }
    
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        // getter/setter
    }
    
    public static class LoginRequest {
        private String username;
        private String password;
        // getter/setter
    }
}

