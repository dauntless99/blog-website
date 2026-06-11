package com.blog.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类
 * 提供Token的生成、解析、验证等功能
 */
public class JwtUtil {

    /** JWT签名密钥（生产环境应从配置文件读取） */
    private static final String SECRET = "blog-cloud-jwt-secret-key-2024-very-long-secret-key!!";

    /** Token过期时间，24小时（毫秒） */
    private static final long EXPIRATION = 86400000L;

    /**
     * 获取签名密钥
     * @return HMAC SHA密钥对象
     */
    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @param username 用户名
     * @return 生成的JWT Token字符串
     */
    public static String generateToken(Long userId, String username) {
        return Jwts.builder()
                .subject(username)                          // 设置主题（用户名）
                .claim("userId", userId)                   // 添加自定义声明（用户ID）
                .issuedAt(new Date())                      // 设置签发时间
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))  // 设置过期时间
                .signWith(getSigningKey())                 // 使用密钥签名
                .compact();                                // 压缩成字符串
    }

    /**
     * 解析Token获取Claims
     * @param token JWT Token字符串
     * @return Claims对象，解析失败返回null
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())           // 设置验证密钥
                    .build()                               // 构建解析器
                    .parseSignedClaims(token)              // 解析签名的Token
                    .getPayload();                         // 获取载荷部分
        } catch (JwtException e) {
            // Token无效、过期、签名错误等情况返回null
            return null;
        }
    }

    /**
     * 从Token中提取用户ID
     * @param token JWT Token字符串
     * @return 用户ID，解析失败返回null
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token中提取用户名
     * @param token JWT Token字符串
     * @return 用户名，解析失败返回null
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.getSubject();
    }

    /**
     * 验证Token是否有效
     * @param token JWT Token字符串
     * @return true表示Token有效，false表示无效
     */
    public static boolean validateToken(String token) {
        return parseToken(token) != null;
    }
}
