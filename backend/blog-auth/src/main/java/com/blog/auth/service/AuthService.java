package com.blog.auth.service;

import com.blog.auth.entity.User;
import com.blog.auth.repository.UserRepository;
import com.blog.common.dto.LoginRequest;
import com.blog.common.dto.LoginResponse;
import com.blog.common.dto.RegisterRequest;
import com.blog.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务类
 * 提供用户登录、注册、查询等认证相关业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    /** BCrypt密码编码器，用于密码加密和验证 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录方法
     * @param request 登录请求，包含用户名和密码
     * @return 登录响应，包含Token和用户信息
     * @throws RuntimeException 用户名或密码错误时抛出异常
     */
    public LoginResponse login(LoginRequest request) {
        // 根据用户名查询用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 验证密码是否匹配
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建并返回登录响应
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname() != null ? user.getNickname() : user.getUsername())
                .avatar(user.getAvatar())
                .build();
    }

    /**
     * 用户注册方法
     * @param request 注册请求，包含用户名、密码、邮箱、昵称等
     * @return 注册成功后的登录响应
     * @throws RuntimeException 用户名已存在或邮箱已被注册时抛出异常
     */
    public LoginResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        // 检查邮箱是否已被注册
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole(0); // 默认普通用户

        // 保存用户到数据库
        user = userRepository.save(user);

        // 生成JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建并返回注册响应
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户实体对象
     * @throws RuntimeException 用户不存在时抛出异常
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}
