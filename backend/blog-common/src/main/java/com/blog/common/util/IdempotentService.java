package com.blog.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性服务
 * 提供接口幂等性保障，防止重复提交
 */
@Service
@RequiredArgsConstructor
public class IdempotentService {

    private final StringRedisTemplate stringRedisTemplate;

    /** Token前缀 */
    private static final String TOKEN_PREFIX = "idempotent:";
    
    /** Token过期时间（毫秒）- 10分钟 */
    private static final long TOKEN_EXPIRE_MS = 600000L;

    /**
     * 生成幂等Token
     * @return Token字符串
     */
    public String generateToken() {
        String token = UUID.randomUUID().toString().replace("-", "");
        String key = TOKEN_PREFIX + token;
        stringRedisTemplate.opsForValue()
                .set(key, "valid", TOKEN_EXPIRE_MS, TimeUnit.MILLISECONDS);
        return token;
    }

    /**
     * 验证并消费Token
     * @param token Token字符串
     * @return 是否验证成功（Token有效且未被使用）
     */
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String key = TOKEN_PREFIX + token;
        Boolean deleted = stringRedisTemplate.delete(key);
        return Boolean.TRUE.equals(deleted);
    }

    /**
     * 检查Token是否存在（不消费）
     * @param token Token字符串
     * @return Token是否存在
     */
    public boolean existsToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String key = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 自定义Token过期时间
     * @param expireMs 过期时间（毫秒）
     * @return Token字符串
     */
    public String generateToken(long expireMs) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String key = TOKEN_PREFIX + token;
        stringRedisTemplate.opsForValue()
                .set(key, "valid", expireMs, TimeUnit.MILLISECONDS);
        return token;
    }

    /**
     * 延长Token有效期
     * @param token Token字符串
     * @param expireMs 新的过期时间（毫秒）
     * @return 是否成功
     */
    public boolean extendToken(String token, long expireMs) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String key = TOKEN_PREFIX + token;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.expire(key, expireMs, TimeUnit.MILLISECONDS);
            return true;
        }
        return false;
    }
}