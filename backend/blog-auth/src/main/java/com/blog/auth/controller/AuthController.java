package com.blog.auth.controller;

import com.blog.auth.service.AuthService;
import com.blog.common.dto.LoginRequest;
import com.blog.common.dto.LoginResponse;
import com.blog.common.dto.RegisterRequest;
import com.blog.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录、注册等认证相关请求
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /** 认证服务 */
    private final AuthService authService;

    /**
     * 用户登录接口
     * @param request 登录请求体，包含用户名和密码
     * @return 登录成功返回用户信息和Token，失败返回错误信息
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return Result.success("登录成功", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 用户注册接口
     * @param request 注册请求体，包含用户名、密码、邮箱、昵称等信息
     * @return 注册成功返回用户信息和Token，失败返回错误信息
     */
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(request);
            return Result.success("注册成功", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
}
