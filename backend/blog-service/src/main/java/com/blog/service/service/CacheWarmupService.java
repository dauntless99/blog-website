package com.blog.service.service;

import com.blog.service.entity.BlogPost;
import com.blog.service.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 缓存预热服务
 * 应用启动时预热热点数据到Redis缓存
 */
@Service
@RequiredArgsConstructor
public class CacheWarmupService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CacheWarmupService.class);

    private final BlogPostRepository blogPostRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 预热缓存键
     */
    private static final String KEY_HOT_POSTS = "hotPosts::top10";
    private static final String KEY_LATEST_POSTS = "latestPosts::top5";
    private static final String KEY_ALL_CATEGORIES = "categories::all";

    /**
     * 应用启动时执行缓存预热
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("========== 开始缓存预热 ==========");
        
        try {
            // 预热热门文章缓存
            warmupHotPosts();
            
            // 预热最新文章缓存
            warmupLatestPosts();
            
            // 预热分类统计缓存
            warmupCategories();
            
            log.info("========== 缓存预热完成 ==========");
        } catch (Exception e) {
            log.error("缓存预热失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 预热热门文章缓存
     */
    public void warmupHotPosts() {
        log.info("预热热门文章缓存...");
        List<BlogPost> hotPosts = blogPostRepository.findTop10ByStatusOrderByViewCountDesc("published");
        redisTemplate.opsForValue().set(KEY_HOT_POSTS, hotPosts);
        log.info("热门文章缓存预热完成，共{}篇", hotPosts.size());
    }

    /**
     * 预热最新文章缓存
     */
    public void warmupLatestPosts() {
        log.info("预热最新文章缓存...");
        List<BlogPost> latestPosts = blogPostRepository.findTop5ByStatusOrderByCreatedAtDesc("published");
        redisTemplate.opsForValue().set(KEY_LATEST_POSTS, latestPosts);
        log.info("最新文章缓存预热完成，共{}篇", latestPosts.size());
    }

    /**
     * 预热分类统计缓存
     */
    public void warmupCategories() {
        log.info("预热分类统计缓存...");
        List<BlogPost> allPosts = blogPostRepository.findAll();
        
        // 统计各分类文章数量
        var categoryStats = allPosts.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        BlogPost::getCategory,
                        java.util.stream.Collectors.counting()
                ));
        
        redisTemplate.opsForValue().set(KEY_ALL_CATEGORIES, categoryStats);
        log.info("分类统计缓存预热完成，共{}个分类", categoryStats.size());
    }

    /**
     * 手动触发缓存预热
     */
    public void triggerWarmup() {
        log.info("手动触发缓存预热...");
        run(null);
    }

    /**
     * 预热指定文章详情缓存
     * @param postId 文章ID
     */
    public void warmupPostDetail(Long postId) {
        blogPostRepository.findById(postId).ifPresent(post -> {
            String key = "postDetail::" + postId;
            redisTemplate.opsForValue().set(key, post);
            log.debug("预热文章详情缓存: postId={}", postId);
        });
    }
}