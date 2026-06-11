package com.blog.service.service;

import com.blog.auth.service.PointService;
import com.blog.common.util.JwtUtil;
import com.blog.service.entity.BlogComment;
import com.blog.service.entity.BlogLike;
import com.blog.service.entity.BlogPost;
import com.blog.service.repository.BlogCommentRepository;
import com.blog.service.repository.BlogLikeRepository;
import com.blog.service.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 点赞服务类
 * 提供文章和评论的点赞/取消点赞功能
 */
@Service
@RequiredArgsConstructor
public class LikeService {

    private final BlogLikeRepository likeRepository;
    private final BlogPostRepository postRepository;
    private final BlogCommentRepository commentRepository;
    private final PointService pointService;

    /**
     * 点赞类型常量
     */
    public static final String TYPE_POST = "POST";
    public static final String TYPE_COMMENT = "COMMENT";

    /**
     * 点赞文章
     * @param postId 文章ID
     * @param token 用户Token
     * @return 是否点赞成功（true-点赞，false-取消点赞）
     */
    @Transactional
    public boolean likePost(Long postId, String token) {
        Long userId = JwtUtil.getUserId(token);
        
        // 检查是否已点赞
        if (likeRepository.existsByPostIdAndUserIdAndType(postId, userId, TYPE_POST)) {
            // 取消点赞
            return cancelLike(postId, userId, TYPE_POST);
        }
        
        // 添加点赞
        return addLike(postId, userId, TYPE_POST);
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @param token 用户Token
     * @return 是否点赞成功（true-点赞，false-取消点赞）
     */
    @Transactional
    public boolean likeComment(Long commentId, String token) {
        Long userId = JwtUtil.getUserId(token);
        
        // 检查是否已点赞
        if (likeRepository.existsByPostIdAndUserIdAndType(commentId, userId, TYPE_COMMENT)) {
            // 取消点赞
            return cancelLike(commentId, userId, TYPE_COMMENT);
        }
        
        // 添加点赞
        return addLike(commentId, userId, TYPE_COMMENT);
    }

    /**
     * 添加点赞（内部方法）
     * @param targetId 目标ID（文章或评论ID）
     * @param userId 用户ID
     * @param type 点赞类型
     * @return true（点赞成功）
     */
    private boolean addLike(Long targetId, Long userId, String type) {
        // 创建点赞记录
        BlogLike like = new BlogLike();
        like.setPostId(targetId);
        like.setUserId(userId);
        like.setType(type);
        like.setCreatedAt(LocalDateTime.now());
        likeRepository.save(like);

        // 更新点赞数
        if (TYPE_POST.equals(type)) {
            BlogPost post = postRepository.findById(targetId).orElse(null);
            if (post != null) {
                post.setLikeCount(post.getLikeCount() + 1);
                postRepository.save(post);
                // 奖励文章作者积分
                pointService.rewardLikeReceived(post.getAuthorId(), targetId);
            }
        } else if (TYPE_COMMENT.equals(type)) {
            BlogComment comment = commentRepository.findById(targetId).orElse(null);
            if (comment != null) {
                comment.setLikeCount(comment.getLikeCount() + 1);
                commentRepository.save(comment);
                // 奖励评论作者积分
                pointService.rewardLikeReceived(comment.getAuthorId(), targetId);
            }
        }

        return true;
    }

    /**
     * 取消点赞（内部方法）
     * @param targetId 目标ID（文章或评论ID）
     * @param userId 用户ID
     * @param type 点赞类型
     * @return false（取消点赞成功）
     */
    private boolean cancelLike(Long targetId, Long userId, String type) {
        // 删除点赞记录
        likeRepository.deleteByPostIdAndUserIdAndType(targetId, userId, type);

        // 更新点赞数
        if (TYPE_POST.equals(type)) {
            BlogPost post = postRepository.findById(targetId).orElse(null);
            if (post != null) {
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                postRepository.save(post);
            }
        } else if (TYPE_COMMENT.equals(type)) {
            BlogComment comment = commentRepository.findById(targetId).orElse(null);
            if (comment != null) {
                comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
                commentRepository.save(comment);
            }
        }

        return false;
    }

    /**
     * 检查用户是否已点赞文章
     * @param postId 文章ID
     * @param token 用户Token
     * @return 是否已点赞
     */
    public boolean isPostLiked(Long postId, String token) {
        Long userId = JwtUtil.getUserId(token);
        return likeRepository.existsByPostIdAndUserIdAndType(postId, userId, TYPE_POST);
    }

    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @param token 用户Token
     * @return 是否已点赞
     */
    public boolean isCommentLiked(Long commentId, String token) {
        Long userId = JwtUtil.getUserId(token);
        return likeRepository.existsByPostIdAndUserIdAndType(commentId, userId, TYPE_COMMENT);
    }

    /**
     * 获取文章点赞数
     * @param postId 文章ID
     * @return 点赞数
     */
    public long getPostLikeCount(Long postId) {
        return likeRepository.countByPostIdAndType(postId, TYPE_POST);
    }

    /**
     * 获取评论点赞数
     * @param commentId 评论ID
     * @return 点赞数
     */
    public long getCommentLikeCount(Long commentId) {
        return likeRepository.countByPostIdAndType(commentId, TYPE_COMMENT);
    }
}