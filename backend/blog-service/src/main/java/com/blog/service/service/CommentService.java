package com.blog.service.service;

import com.blog.auth.entity.User;
import com.blog.auth.repository.UserRepository;
import com.blog.auth.service.PermissionService;
import com.blog.auth.service.PointService;
import com.blog.common.util.JwtUtil;
import com.blog.service.entity.BlogComment;
import com.blog.service.entity.BlogPost;
import com.blog.service.repository.BlogCommentRepository;
import com.blog.service.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 评论服务类
 * 提供评论的CRUD操作和审核功能
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final BlogCommentRepository commentRepository;
    private final BlogPostRepository postRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final PointService pointService;

    /**
     * 评论状态常量
     */
    public static final String STATUS_APPROVED = "approved";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_REJECTED = "rejected";

    /**
     * 获取文章的评论列表
     * @param postId 文章ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论分页
     */
    public Page<BlogComment> getComments(Long postId, int page, int size) {
        return commentRepository.findByPostIdAndStatusOrderByCreatedAtAsc(postId, STATUS_APPROVED, PageRequest.of(page, size));
    }

    /**
     * 获取评论详情
     * @param commentId 评论ID
     * @return 评论详情
     */
    public BlogComment getCommentDetail(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
    }

    /**
     * 创建评论
     * @param request 请求体，包含postId、content、parentId（可选）
     * @param token 用户Token
     * @return 创建的评论
     */
    @Transactional
    public BlogComment createComment(Map<String, Object> request, String token) {
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取文章信息
        Long postId = Long.valueOf(request.get("postId").toString());
        BlogPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        // 检查是否需要审核（根据用户等级或管理员权限）
        String status = permissionService.isAdmin(userId) ? STATUS_APPROVED : STATUS_APPROVED;

        // 创建评论
        BlogComment comment = new BlogComment();
        comment.setPostId(postId);
        comment.setContent((String) request.get("content"));
        comment.setAuthorId(userId);
        comment.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
        comment.setAuthorAvatar(user.getAvatar());
        
        // 处理回复评论
        if (request.containsKey("parentId") && request.get("parentId") != null) {
            Long parentId = Long.valueOf(request.get("parentId").toString());
            // 验证父评论存在
            if (!commentRepository.existsById(parentId)) {
                throw new RuntimeException("父评论不存在");
            }
            comment.setParentId(parentId);
        }
        
        comment.setStatus(status);

        // 保存评论
        BlogComment saved = commentRepository.save(comment);

        // 更新文章评论数
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        // 奖励积分
        pointService.rewardComment(userId, saved.getId());

        return saved;
    }

    /**
     * 更新评论
     * @param commentId 评论ID
     * @param request 请求体，包含content
     * @param token 用户Token
     * @return 更新后的评论
     */
    @Transactional
    public BlogComment updateComment(Long commentId, Map<String, Object> request, String token) {
        Long userId = JwtUtil.getUserId(token);
        
        BlogComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        // 验证权限：只能修改自己的评论，或管理员可以修改任何评论
        if (!comment.getAuthorId().equals(userId) && !permissionService.isAdmin(userId)) {
            throw new RuntimeException("无权修改此评论");
        }

        // 更新内容
        if (request.containsKey("content")) {
            comment.setContent((String) request.get("content"));
        }

        return commentRepository.save(comment);
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param token 用户Token
     */
    @Transactional
    public void deleteComment(Long commentId, String token) {
        Long userId = JwtUtil.getUserId(token);
        
        BlogComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        // 验证权限：只能删除自己的评论，或管理员可以删除任何评论
        if (!comment.getAuthorId().equals(userId) && !permissionService.isAdmin(userId)) {
            throw new RuntimeException("无权删除此评论");
        }

        // 更新文章评论数
        BlogPost post = postRepository.findById(comment.getPostId()).orElse(null);
        if (post != null) {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postRepository.save(post);
        }

        // 删除评论
        commentRepository.delete(comment);
    }

    /**
     * 审核评论（管理员操作）
     * @param commentId 评论ID
     * @param status 审核状态
     * @param token 用户Token
     */
    @Transactional
    public void approveComment(Long commentId, String status, String token) {
        Long userId = JwtUtil.getUserId(token);
        
        if (!permissionService.isAdmin(userId)) {
            throw new RuntimeException("无权限审核评论");
        }

        BlogComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        comment.setStatus(status);
        commentRepository.save(comment);
    }

    /**
     * 获取待审核评论列表
     * @param page 页码
     * @param size 每页大小
     * @return 待审核评论分页
     */
    public Page<BlogComment> getPendingComments(int page, int size) {
        return commentRepository.findByStatusOrderByCreatedAtDesc(STATUS_PENDING, PageRequest.of(page, size));
    }

    /**
     * 批量审核评论
     * @param commentIds 评论ID列表
     * @param status 审核状态
     * @param token 用户Token
     */
    @Transactional
    public void batchApproveComments(List<Long> commentIds, String status, String token) {
        Long userId = JwtUtil.getUserId(token);
        
        if (!permissionService.isAdmin(userId)) {
            throw new RuntimeException("无权限审核评论");
        }

        for (Long commentId : commentIds) {
            commentRepository.findById(commentId).ifPresent(comment -> {
                comment.setStatus(status);
                commentRepository.save(comment);
            });
        }
    }
}