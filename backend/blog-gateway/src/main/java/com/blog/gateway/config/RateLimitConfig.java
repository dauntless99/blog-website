package com.blog.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 网关限流配置类
 * 配置限流的Key解析器
 */
@Configuration
public class RateLimitConfig {

    /**
     * 用户ID限流Key解析器
     * 优先使用用户ID，其次使用请求来源IP
     * @return KeyResolver实例
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // 尝试从请求头获取用户ID
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null && !userId.isEmpty()) {
                return Mono.just("user:" + userId);
            }
            
            // 尝试从Authorization头获取用户标识
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                // 使用Token的前8位作为标识（避免存储完整Token）
                return Mono.just("token:" + token.substring(0, Math.min(8, token.length())));
            }
            
            // 默认使用客户端IP
            String clientIp = exchange.getRequest().getRemoteAddress() != null 
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just("ip:" + clientIp);
        };
    }

    /**
     * IP限流Key解析器
     * @return KeyResolver实例
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String clientIp = exchange.getRequest().getRemoteAddress() != null 
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just("ip:" + clientIp);
        };
    }
}