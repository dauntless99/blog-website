package com.blog.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 * 基于Redis实现分布式环境下的互斥锁
 */
@Component
@RequiredArgsConstructor
public class RedisLock {

    private final StringRedisTemplate stringRedisTemplate;

    /** 锁前缀 */
    private static final String LOCK_PREFIX = "lock:";
    
    /** 默认锁过期时间（毫秒）- 30秒 */
    private static final long DEFAULT_EXPIRE_MS = 30000L;
    
    /** 默认重试间隔（毫秒） */
    private static final long RETRY_INTERVAL_MS = 100L;
    
    /** 默认最大重试次数 */
    private static final int MAX_RETRY_COUNT = 30;

    /**
     * 尝试获取锁
     * @param key 锁的key
     * @return 是否获取成功
     */
    public boolean tryLock(String key) {
        String lockKey = LOCK_PREFIX + key;
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "locked", DEFAULT_EXPIRE_MS, TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 尝试获取锁（带过期时间）
     * @param key 锁的key
     * @param expireMs 过期时间（毫秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String key, long expireMs) {
        String lockKey = LOCK_PREFIX + key;
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "locked", expireMs, TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 获取锁（带重试）
     * @param key 锁的key
     * @return 是否获取成功
     */
    public boolean lock(String key) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY_COUNT) {
            if (tryLock(key)) {
                return true;
            }
            try {
                Thread.sleep(RETRY_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            retryCount++;
        }
        return false;
    }

    /**
     * 获取锁（带重试和过期时间）
     * @param key 锁的key
     * @param expireMs 过期时间（毫秒）
     * @return 是否获取成功
     */
    public boolean lock(String key, long expireMs) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY_COUNT) {
            if (tryLock(key, expireMs)) {
                return true;
            }
            try {
                Thread.sleep(RETRY_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            retryCount++;
        }
        return false;
    }

    /**
     * 释放锁
     * @param key 锁的key
     */
    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        stringRedisTemplate.delete(lockKey);
    }

    /**
     * 尝试获取锁（带超时时间）
     * @param key 锁的key
     * @param timeoutMs 超时时间（毫秒）
     * @return 是否获取成功
     */
    public boolean tryLockWithTimeout(String key, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (tryLock(key)) {
                return true;
            }
            try {
                Thread.sleep(RETRY_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}