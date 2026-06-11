package com.blog.service.mq;

import com.blog.service.entity.BlogPost;
import com.blog.service.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.blog.common.config.RabbitMQConfig.*;

/**
 * 博客服务MQ消息消费者
 * 处理异步消息：缓存清除、阅读量更新等
 */
@Component
@RequiredArgsConstructor
public class BlogMQConsumer {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(BlogMQConsumer.class);

    /** 文章数据访问层 */
    private final BlogPostRepository blogPostRepository;

    /**
     * 消费缓存清除消息
     * 当文章创建/更新/删除时，清除首页相关的缓存
     * @param msg 消息内容，包含type（操作类型）和postId（文章ID）
     */
    @RabbitListener(queues = QUEUE_CACHE_EVICT)
    @CacheEvict(value = {"hotPosts", "latestPosts"}, allEntries = true)
    public void handleCacheEvict(Map<String, Object> msg) {
        log.info("收到缓存清除通知: type={}, postId={}", msg.get("type"), msg.get("postId"));
        // @CacheEvict 注解已自动清除 hotPosts 和 latestPosts 缓存
    }

    /**
     * 消费阅读量更新消息
     * 异步更新文章阅读量，避免阻塞请求
     * @param postId 文章ID
     */
    @RabbitListener(queues = QUEUE_VIEW_COUNT)
    @Transactional
    public void handleViewCount(Long postId) {
        int rows = blogPostRepository.incrementViewCount(postId);
        if (rows > 0) {
            log.debug("异步更新阅读量: postId={}", postId);
        }
    }
}
