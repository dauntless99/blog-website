package com.blog.service.service;

import com.blog.common.util.JwtUtil;
import com.blog.service.entity.BlogPost;
import com.blog.service.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.blog.common.config.RabbitMQConfig.*;

/**
 * 博客文章服务类
 * 提供文章的CRUD操作、缓存管理和MQ消息处理
 */
@Service
@RequiredArgsConstructor
public class BlogService {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(BlogService.class);

    /** 文章数据访问层 */
    private final BlogPostRepository blogPostRepository;

    /** RabbitMQ消息模板，用于异步消息发送 */
    private final RabbitTemplate rabbitTemplate;

    /**
     * 获取文章列表（支持分类和关键词搜索）
     * 搜索类查询不缓存，直接查询数据库
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param category 分类名称（可选）
     * @param keyword 搜索关键词（可选）
     * @return 分页文章列表
     */
    public Page<BlogPost> getPostList(int page, int size, String category, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // 优先处理关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            return blogPostRepository.search(keyword, pageRequest);
        }
        // 其次处理分类筛选
        if (category != null && !category.isEmpty()) {
            return blogPostRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, "published", pageRequest);
        }
        // 默认返回所有已发布文章
        return blogPostRepository.findByStatusOrderByCreatedAtDesc("published", pageRequest);
    }

    /**
     * 获取文章详情（带Redis缓存）
     * @param id 文章ID
     * @return 文章详情
     * @throws RuntimeException 文章不存在时抛出异常
     */
    @Cacheable(value = "postDetail", key = "#id", unless = "#result == null")
    public BlogPost getPostDetail(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        // 异步更新阅读量 — 发送MQ消息，不阻塞当前请求
        rabbitTemplate.convertAndSend(EXCHANGE_BLOG, ROUTING_VIEW_COUNT, id);
        return post;
    }

    /**
     * 创建文章
     * 创建成功后清除文章详情缓存，并发送MQ通知清除首页缓存
     * @param request 创建请求，包含标题、内容、分类等信息
     * @param token 用户Token，用于提取用户信息
     * @return 创建的文章对象
     */
    @CacheEvict(value = "postDetail", allEntries = true)
    public BlogPost createPost(Map<String, Object> request, String token) {
        // 从Token中提取用户信息
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        // 构建文章对象
        BlogPost post = new BlogPost();
        post.setTitle((String) request.get("title"));
        post.setContent((String) request.get("content"));
        post.setSummary((String) request.getOrDefault("summary", ""));
        post.setAuthorId(userId);
        post.setAuthorName(username);
        post.setCategory((String) request.getOrDefault("category", "默认"));
        post.setTags((String) request.getOrDefault("tags", ""));
        post.setStatus((String) request.getOrDefault("status", "published"));

        // 自动生成摘要
        if (post.getSummary().isEmpty() && post.getContent().length() > 200) {
            post.setSummary(post.getContent().substring(0, 200) + "...");
        } else if (post.getSummary().isEmpty()) {
            post.setSummary(post.getContent());
        }

        // 保存文章到数据库
        BlogPost saved = blogPostRepository.save(post);

        // MQ异步通知：清除首页/列表的Redis缓存
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "POST_CREATED");
        msg.put("postId", saved.getId());
        msg.put("category", saved.getCategory());
        rabbitTemplate.convertAndSend(EXCHANGE_BLOG, ROUTING_CACHE_EVICT, msg);

        return saved;
    }

    /**
     * 更新文章
     * 更新成功后清除该文章的缓存，并发送MQ通知清除首页缓存
     * @param id 文章ID
     * @param request 更新请求
     * @param token 用户Token，用于验证权限
     * @return 更新后的文章对象
     * @throws RuntimeException 文章不存在或无权修改时抛出异常
     */
    @CacheEvict(value = "postDetail", key = "#id")
    public BlogPost updatePost(Long id, Map<String, Object> request, String token) {
        // 查询文章
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        // 验证用户权限
        Long userId = JwtUtil.getUserId(token);
        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("无权修改此文章");
        }

        // 更新字段
        if (request.containsKey("title")) post.setTitle((String) request.get("title"));
        if (request.containsKey("content")) post.setContent((String) request.get("content"));
        if (request.containsKey("summary")) post.setSummary((String) request.get("summary"));
        if (request.containsKey("category")) post.setCategory((String) request.get("category"));
        if (request.containsKey("tags")) post.setTags((String) request.get("tags"));
        if (request.containsKey("status")) post.setStatus((String) request.get("status"));

        // 保存更新
        BlogPost updated = blogPostRepository.save(post);

        // MQ异步清除首页缓存
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "POST_UPDATED");
        msg.put("postId", id);
        rabbitTemplate.convertAndSend(EXCHANGE_BLOG, ROUTING_CACHE_EVICT, msg);

        return updated;
    }

    /**
     * 删除文章
     * 删除成功后清除该文章的缓存，并发送MQ通知清除首页缓存
     * @param id 文章ID
     * @param token 用户Token，用于验证权限
     * @throws RuntimeException 文章不存在或无权删除时抛出异常
     */
    @CacheEvict(value = "postDetail", key = "#id")
    @Transactional
    public void deletePost(Long id, String token) {
        // 查询文章
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        // 验证用户权限
        Long userId = JwtUtil.getUserId(token);
        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("无权删除此文章");
        }

        // 删除文章
        blogPostRepository.delete(post);

        // MQ异步清除首页缓存
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "POST_DELETED");
        msg.put("postId", id);
        rabbitTemplate.convertAndSend(EXCHANGE_BLOG, ROUTING_CACHE_EVICT, msg);
    }

    /**
     * 获取热门文章（Redis缓存）
     * @return 阅读量最高的前10篇文章
     */
    @Cacheable(value = "hotPosts", key = "'top10'", unless = "#result == null || #result.isEmpty()")
    public List<BlogPost> getHotPosts() {
        return blogPostRepository.findTop10ByStatusOrderByViewCountDesc("published");
    }

    /**
     * 获取最新文章（Redis缓存）
     * @return 最新发布的前5篇文章
     */
    @Cacheable(value = "latestPosts", key = "'top5'", unless = "#result == null || #result.isEmpty()")
    public List<BlogPost> getLatestPosts() {
        return blogPostRepository.findTop5ByStatusOrderByCreatedAtDesc("published");
    }

    /**
     * 定时刷新热门文章缓存（每5分钟执行一次）
     */
    @Scheduled(fixedRate = 300000)
    public void refreshHotPostsCache() {
        log.info("定时任务: 刷新热门文章缓存");
        // 缓存会在下次读取时自动更新，这里仅记录日志
    }
}
