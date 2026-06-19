package com.blog.common.service;

import com.blog.common.entity.User;
import com.blog.common.repository.UserRepository;
import com.blog.common.entity.Notification;
import com.blog.common.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通知服务类
 * 提供通知的创建、查询、标记已读等功能
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 创建通知
     * @param userId 接收者ID
     * @param type 通知类型
     * @param title 通知标题
     * @param content 通知内容
     * @param link 相关链接
     * @param targetId 相关内容ID
     * @param actorId 触发者ID（可选）
     * @return 创建的通知
     */
    @Transactional
    public Notification createNotification(Long userId, String type, String title, 
                                          String content, String link, Long targetId, Long actorId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setLink(link);
        notification.setTargetId(targetId);
        notification.setCreatedAt(LocalDateTime.now());

        // 设置触发者信息
        if (actorId != null) {
            User actor = userRepository.findById(actorId).orElse(null);
            if (actor != null) {
                notification.setActorId(actorId);
                notification.setActorName(actor.getNickname() != null ? actor.getNickname() : actor.getUsername());
            }
        }

        return notificationRepository.save(notification);
    }

    /**
     * 获取用户的通知列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 通知分页
     */
    public Page<Notification> getNotifications(Long userId, int page, int size) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    /**
     * 获取未读通知数量
     * @param userId 用户ID
     * @return 未读通知数
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 标记单条通知为已读
     * @param userId 用户ID
     * @param notificationId 通知ID
     */
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        notificationRepository.markAsRead(userId, notificationId);
    }

    /**
     * 标记所有通知为已读
     * @param userId 用户ID
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    /**
     * 删除单条通知
     * @param userId 用户ID
     * @param notificationId 通知ID
     */
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        notificationRepository.deleteByIdAndUserId(userId, notificationId);
    }

    /**
     * 创建点赞文章通知
     * @param postAuthorId 文章作者ID
     * @param actorId 点赞者ID
     * @param postId 文章ID
     */
    @Transactional
    public void createLikePostNotification(Long postAuthorId, Long actorId, Long postId) {
        User actor = userRepository.findById(actorId).orElse(null);
        String actorName = actor != null ? 
                (actor.getNickname() != null ? actor.getNickname() : actor.getUsername()) : "未知用户";

        createNotification(
                postAuthorId,
                Notification.TYPE_LIKE_POST,
                "点赞通知",
                actorName + " 点赞了你的文章",
                "/blog/posts/" + postId,
                postId,
                actorId
        );
    }

    /**
     * 创建点赞评论通知
     * @param commentAuthorId 评论作者ID
     * @param actorId 点赞者ID
     * @param commentId 评论ID
     */
    @Transactional
    public void createLikeCommentNotification(Long commentAuthorId, Long actorId, Long commentId) {
        User actor = userRepository.findById(actorId).orElse(null);
        String actorName = actor != null ? 
                (actor.getNickname() != null ? actor.getNickname() : actor.getUsername()) : "未知用户";

        createNotification(
                commentAuthorId,
                Notification.TYPE_LIKE_COMMENT,
                "点赞通知",
                actorName + " 点赞了你的评论",
                "/comments/" + commentId,
                commentId,
                actorId
        );
    }

    /**
     * 创建评论通知
     * @param postAuthorId 文章作者ID
     * @param actorId 评论者ID
     * @param postId 文章ID
     */
    @Transactional
    public void createCommentNotification(Long postAuthorId, Long actorId, Long postId) {
        User actor = userRepository.findById(actorId).orElse(null);
        String actorName = actor != null ? 
                (actor.getNickname() != null ? actor.getNickname() : actor.getUsername()) : "未知用户";

        createNotification(
                postAuthorId,
                Notification.TYPE_COMMENT,
                "评论通知",
                actorName + " 评论了你的文章",
                "/blog/posts/" + postId + "#comments",
                postId,
                actorId
        );
    }

    /**
     * 创建回复通知
     * @param parentCommentAuthorId 被回复者ID
     * @param actorId 回复者ID
     * @param commentId 评论ID
     */
    @Transactional
    public void createReplyNotification(Long parentCommentAuthorId, Long actorId, Long commentId) {
        User actor = userRepository.findById(actorId).orElse(null);
        String actorName = actor != null ? 
                (actor.getNickname() != null ? actor.getNickname() : actor.getUsername()) : "未知用户";

        createNotification(
                parentCommentAuthorId,
                Notification.TYPE_REPLY,
                "回复通知",
                actorName + " 回复了你的评论",
                "/comments/" + commentId,
                commentId,
                actorId
        );
    }

    /**
     * 创建@提及通知
     * @param mentionedUserId 被提及用户ID
     * @param actorId 提及者ID
     * @param postId 文章ID（或评论ID）
     */
    @Transactional
    public void createMentionNotification(Long mentionedUserId, Long actorId, Long postId) {
        User actor = userRepository.findById(actorId).orElse(null);
        String actorName = actor != null ? 
                (actor.getNickname() != null ? actor.getNickname() : actor.getUsername()) : "未知用户";

        createNotification(
                mentionedUserId,
                Notification.TYPE_MENTION,
                "@提及通知",
                actorName + " 在文章中@了你",
                "/blog/posts/" + postId,
                postId,
                actorId
        );
    }

    /**
     * 创建系统通知
     * @param userId 用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param link 相关链接
     */
    @Transactional
    public void createSystemNotification(Long userId, String title, String content, String link) {
        createNotification(userId, Notification.TYPE_SYSTEM, title, content, link, null, null);
    }

    /**
     * 创建文章审核结果通知
     * @param authorId 作者ID
     * @param postId 文章ID
     * @param approved 是否通过审核
     */
    @Transactional
    public void createPostApprovalNotification(Long authorId, Long postId, boolean approved) {
        String title = approved ? "文章审核通过" : "文章审核未通过";
        String content = approved ? "你的文章已通过审核，可以正常展示" : "你的文章未通过审核，请修改后重新提交";
        
        createNotification(
                authorId,
                Notification.TYPE_POST_APPROVAL,
                title,
                content,
                "/blog/posts/" + postId,
                postId,
                null
        );
    }
}