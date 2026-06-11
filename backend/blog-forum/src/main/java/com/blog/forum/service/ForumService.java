package com.blog.forum.service;

import com.blog.common.util.JwtUtil;
import com.blog.forum.entity.ForumCategory;
import com.blog.forum.entity.ForumReply;
import com.blog.forum.entity.ForumThread;
import com.blog.forum.repository.ForumCategoryRepository;
import com.blog.forum.repository.ForumReplyRepository;
import com.blog.forum.repository.ForumThreadRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.blog.common.config.RabbitMQConfig.*;

/**
 * 论坛服务类
 * 提供帖子分类、帖子管理、回复管理等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class ForumService {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(ForumService.class);

    /** 分类数据访问层 */
    private final ForumCategoryRepository categoryRepository;

    /** 帖子数据访问层 */
    private final ForumThreadRepository threadRepository;

    /** 回复数据访问层 */
    private final ForumReplyRepository replyRepository;

    /** RabbitMQ消息模板 */
    private final RabbitTemplate rabbitTemplate;

    /**
     * 获取所有分类（Redis缓存）
     * @return 分类列表，按排序字段升序排列
     */
    @Cacheable(value = "forumCategories", key = "'all'", unless = "#result == null || #result.isEmpty()")
    public List<ForumCategory> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    /**
     * 创建分类
     * 创建后清除分类缓存
     * @param category 分类对象
     * @return 创建的分类
     */
    @CacheEvict(value = "forumCategories", allEntries = true)
    public ForumCategory createCategory(ForumCategory category) {
        return categoryRepository.save(category);
    }

    /**
     * 获取帖子列表（支持分类筛选和关键词搜索）
     * 搜索类查询不缓存，直接查询数据库
     * @param categoryId 分类ID（可选）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param keyword 搜索关键词（可选）
     * @return 分页帖子列表
     */
    public Page<ForumThread> getThreads(Long categoryId, int page, int size, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size);
        // 优先处理关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            return threadRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageRequest);
        }
        // 其次处理分类筛选
        if (categoryId != null && categoryId > 0) {
            return threadRepository.findByCategoryIdOrderByIsPinnedDescCreatedAtDesc(categoryId, pageRequest);
        }
        // 默认返回所有帖子，按置顶和时间排序
        return threadRepository.findByOrderByIsPinnedDescCreatedAtDesc(pageRequest);
    }

    /**
     * 获取帖子详情
     * @param id 帖子ID
     * @return 帖子详情（阅读量+1）
     * @throws RuntimeException 帖子不存在时抛出异常
     */
    public ForumThread getThreadDetail(Long id) {
        ForumThread thread = threadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        // 更新阅读量
        thread.setViewCount(thread.getViewCount() + 1);
        threadRepository.save(thread);
        return thread;
    }

    /**
     * 创建帖子
     * 创建后更新分类的帖子计数，并清除分类缓存
     * @param request 创建请求，包含标题、内容、分类ID等
     * @param token 用户Token，用于提取用户信息
     * @return 创建的帖子
     * @throws RuntimeException 分类不存在时抛出异常
     */
    @CacheEvict(value = "forumCategories", allEntries = true)
    public ForumThread createThread(Map<String, Object> request, String token) {
        // 从Token中提取用户信息
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        // 构建帖子对象
        ForumThread thread = new ForumThread();
        thread.setTitle((String) request.get("title"));
        thread.setContent((String) request.get("content"));
        thread.setAuthorId(userId);
        thread.setAuthorName(username);
        thread.setCategoryId(Long.valueOf(request.get("categoryId").toString()));

        // 验证分类是否存在
        ForumCategory category = categoryRepository.findById(thread.getCategoryId())
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        thread.setCategoryName(category.getName());

        // 保存帖子
        thread = threadRepository.save(thread);

        // 更新分类的帖子计数
        category.setThreadCount(category.getThreadCount() + 1);
        categoryRepository.save(category);

        return thread;
    }

    /**
     * 获取帖子回复列表
     * @param threadId 帖子ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页回复列表
     */
    public Page<ForumReply> getReplies(Long threadId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        return replyRepository.findByThreadIdOrderByCreatedAtAsc(threadId, pageRequest);
    }

    /**
     * 创建回复
     * @param request 创建请求，包含内容、帖子ID、父回复ID（可选）
     * @param token 用户Token，用于提取用户信息
     * @return 创建的回复
     */
    public ForumReply createReply(Map<String, Object> request, String token) {
        // 从Token中提取用户信息
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        // 构建回复对象
        ForumReply reply = new ForumReply();
        reply.setContent((String) request.get("content"));
        reply.setThreadId(Long.valueOf(request.get("threadId").toString()));
        reply.setAuthorId(userId);
        reply.setAuthorName(username);
        // 处理父回复（可选）
        if (request.containsKey("parentId") && request.get("parentId") != null) {
            reply.setParentId(Long.valueOf(request.get("parentId").toString()));
        }

        // 保存回复
        reply = replyRepository.save(reply);

        // MQ异步更新回复计数（流量大时不阻塞请求）
        Map<String, Object> msg = new HashMap<>();
        msg.put("threadId", reply.getThreadId());
        rabbitTemplate.convertAndSend(EXCHANGE_FORUM, ROUTING_REPLY_COUNT, msg);

        return reply;
    }

    /**
     * 删除帖子
     * 级联删除所有回复，并清除分类缓存
     * @param id 帖子ID
     * @param token 用户Token，用于验证权限
     * @throws RuntimeException 帖子不存在或无权删除时抛出异常
     */
    @Transactional
    @CacheEvict(value = "forumCategories", allEntries = true)
    public void deleteThread(Long id, String token) {
        // 查询帖子
        ForumThread thread = threadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));

        // 验证用户权限
        Long userId = JwtUtil.getUserId(token);
        if (!thread.getAuthorId().equals(userId)) {
            throw new RuntimeException("无权删除此帖子");
        }

        // 删除所有回复
        replyRepository.findByThreadIdOrderByCreatedAtAsc(id).forEach(reply ->
                replyRepository.delete(reply)
        );

        // 删除帖子
        threadRepository.delete(thread);
    }
}
