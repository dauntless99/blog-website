package com.blog.auth.controller;

import com.blog.auth.service.AuthService;
import com.blog.common.dto.LoginRequest;
import com.blog.common.dto.LoginResponse;
import com.blog.common.dto.RegisterRequest;
import com.blog.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return Result.success("登录成功", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

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
