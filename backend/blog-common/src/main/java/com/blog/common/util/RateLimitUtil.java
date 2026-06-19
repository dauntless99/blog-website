package com.blog.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 限流工具类
 * 基于Redis实现简单的限流功能
 */
@Component
@RequiredArgsConstructor
public class RateLimitUtil {

    private final StringRedisTemplate stringRedisTemplate;

    /** 限流前缀 */
    private static final String LIMIT_PREFIX = "rate_limit:";
    
    /** 默认时间窗口（秒） */
    private static final long DEFAULT_WINDOW_SECONDS = 60L;

    /**
     * 检查是否允许访问（简单计数器模式）
     * @param key 限流key（如用户ID、IP地址）
     * @param maxRequests 最大请求数
     * @param windowSeconds 时间窗口（秒）
     * @return 是否允许访问
     */
    public boolean tryAcquire(String key, int maxRequests, long windowSeconds) {
        String limitKey = LIMIT_PREFIX + key;
        
        // 获取当前计数
        String current = stringRedisTemplate.opsForValue().get(limitKey);
        int count = current == null ? 0 : Integer.parseInt(current);
        
        if (count >= maxRequests) {
            return false;
        }
        
        // 增加计数（首次设置时同时设置过期时间）
        stringRedisTemplate.opsForValue().increment(limitKey);
        if (count == 0) {
            stringRedisTemplate.expire(limitKey, windowSeconds, TimeUnit.SECONDS);
        }
        
        return true;
    }

    /**
     * 检查是否允许访问（使用Lua脚本保证原子性）
     * @param key 限流key
     * @param maxRequests 最大请求数
     * @param windowSeconds 时间窗口（秒）
     * @return 是否允许访问
     */
    public boolean tryAcquireAtomic(String key, int maxRequests, long windowSeconds) {
        String limitKey = LIMIT_PREFIX + key;
        
        // Lua脚本：原子操作，获取并增加计数，同时设置过期时间
        String luaScript = """
            local current = redis.call('get', KEYS[1])
            if current and tonumber(current) >= tonumber(ARGV[1]) then
                return 0
            end
            redis.call('incr', KEYS[1])
            if not current then
                redis.call('expire', KEYS[1], ARGV[2])
            end
            return 1
            """;
        
        Long result = stringRedisTemplate.execute(
                (connection) -> {
                    byte[] scriptBytes = luaScript.getBytes();
                    byte[] keyBytes = limitKey.getBytes();
                    byte[] maxRequestsBytes = String.valueOf(maxRequests).getBytes();
                    byte[] windowBytes = String.valueOf(windowSeconds).getBytes();
                    return connection.scriptingCommands().eval(
                            scriptBytes,
                            org.springframework.data.redis.connection.ReturnType.INTEGER,
//                            org.springframework.data.redis.connection.RedisScript.ReturnType.INTEGER,
                            1,
                            keyBytes,
                            maxRequestsBytes,
                            windowBytes
                    );
                },
                true
        );
        
        return result != null && result == 1;
    }

    /**
     * 默认限流检查（每分钟最多100次请求）
     * @param key 限流key
     * @return 是否允许访问
     */
    public boolean tryAcquire(String key) {
        return tryAcquireAtomic(key, 100, DEFAULT_WINDOW_SECONDS);
    }

    /**
     * 获取当前请求计数
     * @param key 限流key
     * @return 当前计数
     */
    public long getCurrentCount(String key) {
        String limitKey = LIMIT_PREFIX + key;
        String current = stringRedisTemplate.opsForValue().get(limitKey);
        return current == null ? 0 : Long.parseLong(current);
    }

    /**
     * 重置限流计数
     * @param key 限流key
     */
    public void reset(String key) {
        String limitKey = LIMIT_PREFIX + key;
        stringRedisTemplate.delete(limitKey);
    }
}