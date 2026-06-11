package com.blog.auth.service;

import com.blog.auth.entity.*;
import com.blog.auth.repository.*;
import com.blog.forum.entity.ForumThread;
import com.blog.forum.repository.ForumThreadRepository;
import com.blog.service.entity.BlogComment;
import com.blog.service.entity.BlogPost;
import com.blog.service.repository.BlogCommentRepository;
import com.blog.service.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务类
 * 提供管理员控制台、数据统计、内容审核等功能
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final BlogPostRepository blogPostRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final NotificationRepository notificationRepository;

    /**
     * 获取系统统计数据
     * @return 统计数据
     */
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        long totalUsers = userRepository.count();
        long todayUsers = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(1));
        stats.put("totalUsers", totalUsers);
        stats.put("todayUsers", todayUsers);
        
        // 文章统计
        long totalPosts = blogPostRepository.count();
        long todayPosts = blogPostRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(1));
        long pendingPosts = blogPostRepository.countByStatus("pending");
        stats.put("totalPosts", totalPosts);
        stats.put("todayPosts", todayPosts);
        stats.put("pendingPosts", pendingPosts);
        
        // 评论统计
        long totalComments = blogCommentRepository.count();
        long todayComments = blogCommentRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(1));
        stats.put("totalComments", totalComments);
        stats.put("todayComments", todayComments);
        
        // 帖子统计
        long totalThreads = forumThreadRepository.count();
        long todayThreads = forumThreadRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(1));
        stats.put("totalThreads", totalThreads);
        stats.put("todayThreads", todayThreads);
        
        // 通知统计
        long unreadNotifications = notificationRepository.countByReadFalse();
        stats.put("unreadNotifications", unreadNotifications);
        
        return stats;
    }

    /**
     * 获取用户列表（管理员查看）
     * @param page 页码
     * @param size 每页大小
     * @return 分页用户列表
     */
    public Page<User> getUserList(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * 获取待审核文章列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页待审核文章列表
     */
    public Page<BlogPost> getPendingPosts(int page, int size) {
        return blogPostRepository.findByStatusOrderByCreatedAtDesc("pending", PageRequest.of(page, size));
    }

    /**
     * 审核文章
     * @param id 文章ID
     * @param status 审核状态：approved-通过，rejected-拒绝
     * @return 更新后的文章
     * @throws RuntimeException 文章不存在时抛出异常
     */
    public BlogPost reviewPost(Long id, String status) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        post.setStatus(status);
        return blogPostRepository.save(post);
    }

    /**
     * 删除文章（管理员权限）
     * @param id 文章ID
     * @throws RuntimeException 文章不存在时抛出异常
     */
    public void deletePostAsAdmin(Long id) {
        if (!blogPostRepository.existsById(id)) {
            throw new RuntimeException("文章不存在");
        }
        blogPostRepository.deleteById(id);
    }

    /**
     * 获取帖子列表（管理员查看）
     * @param page 页码
     * @param size 每页大小
     * @return 分页帖子列表
     */
    public Page<ForumThread> getThreadList(int page, int size) {
        return forumThreadRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * 删除帖子（管理员权限）
     * @param id 帖子ID
     * @throws RuntimeException 帖子不存在时抛出异常
     */
    public void deleteThreadAsAdmin(Long id) {
        if (!forumThreadRepository.existsById(id)) {
            throw new RuntimeException("帖子不存在");
        }
        forumThreadRepository.deleteById(id);
    }

    /**
     * 置顶/取消置顶帖子
     * @param id 帖子ID
     * @param isPinned 是否置顶
     * @return 更新后的帖子
     * @throws RuntimeException 帖子不存在时抛出异常
     */
    public ForumThread toggleThreadPin(Long id, boolean isPinned) {
        ForumThread thread = forumThreadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        thread.setIsPinned(isPinned);
        return forumThreadRepository.save(thread);
    }

    /**
     * 锁定/解锁帖子
     * @param id 帖子ID
     * @param isLocked 是否锁定
     * @return 更新后的帖子
     * @throws RuntimeException 帖子不存在时抛出异常
     */
    public ForumThread toggleThreadLock(Long id, boolean isLocked) {
        ForumThread thread = forumThreadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        thread.setIsLocked(isLocked);
        return forumThreadRepository.save(thread);
    }

    /**
     * 获取评论列表（管理员查看）
     * @param page 页码
     * @param size 每页大小
     * @return 分页评论列表
     */
    public Page<BlogComment> getCommentList(int page, int size) {
        return blogCommentRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * 删除评论（管理员权限）
     * @param id 评论ID
     * @throws RuntimeException 评论不存在时抛出异常
     */
    public void deleteCommentAsAdmin(Long id) {
        if (!blogCommentRepository.existsById(id)) {
            throw new RuntimeException("评论不存在");
        }
        blogCommentRepository.deleteById(id);
    }

    /**
     * 获取用户等级列表
     * @return 用户等级列表
     */
    public List<UserLevel> getUserLevels() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserLevel level = new UserLevel();
                    level.setUserId(user.getId());
                    level.setUsername(user.getUsername());
                    level.setNickname(user.getNickname());
                    return level;
                })
                .toList();
    }
}