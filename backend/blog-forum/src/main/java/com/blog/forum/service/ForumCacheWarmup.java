package com.blog.forum.service;

import com.blog.forum.entity.ForumCategory;
import com.blog.forum.entity.ForumThread;
import com.blog.forum.repository.ForumCategoryRepository;
import com.blog.forum.repository.ForumThreadRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 论坛缓存预热服务
 * 应用启动时预热论坛热点数据到Redis缓存
 */
@Service
@RequiredArgsConstructor
public class ForumCacheWarmup implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ForumCacheWarmup.class);

    private final ForumCategoryRepository categoryRepository;
    private final ForumThreadRepository threadRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 预热缓存键
     */
    private static final String KEY_CATEGORIES = "forumCategories::all";
    private static final String KEY_HOT_THREADS = "forumHotThreads::top10";

    /**
     * 应用启动时执行缓存预热
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("========== 开始论坛缓存预热 ==========");
        
        try {
            // 预热分类缓存
            warmupCategories();
            
            // 预热热门帖子缓存
            warmupHotThreads();
            
            log.info("========== 论坛缓存预热完成 ==========");
        } catch (Exception e) {
            log.error("论坛缓存预热失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 预热分类缓存
     */
    public void warmupCategories() {
        log.info("预热论坛分类缓存...");
        List<ForumCategory> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        redisTemplate.opsForValue().set(KEY_CATEGORIES, categories);
        log.info("论坛分类缓存预热完成，共{}个分类", categories.size());
    }

    /**
     * 预热热门帖子缓存
     */
    public void warmupHotThreads() {
        log.info("预热热门帖子缓存...");
        List<ForumThread> hotThreads = threadRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()))
                .limit(10)
                .toList();
        redisTemplate.opsForValue().set(KEY_HOT_THREADS, hotThreads);
        log.info("热门帖子缓存预热完成，共{}篇", hotThreads.size());
    }

    /**
     * 手动触发缓存预热
     */
    public void triggerWarmup() {
        log.info("手动触发论坛缓存预热...");
        run(null);
    }
}