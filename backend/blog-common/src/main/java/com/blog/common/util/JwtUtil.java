package com.blog.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "blog-cloud-jwt-secret-key-2024-very-long-secret-key!!";
    private static final long EXPIRATION = 86400000L; // 24小时

    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(Long userId, String username) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            return null;
        }
    }

    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("userId", Long.class);
    }

    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.getSubject();
    }

    public static boolean validateToken(String token) {
        return parseToken(token) != null;
    }
}
