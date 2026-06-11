package com.blog.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知实体类
 * 对应数据库表: notifications
 */
@Data
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 接收者ID */
    @Column(nullable = false)
    private Long userId;

    /** 通知类型 */
    @Column(nullable = false, length = 50)
    private String type;

    /** 通知标题 */
    @Column(length = 100)
    private String title;

    /** 通知内容 */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 相关链接 */
    @Column(length = 500)
    private String link;

    /** 相关内容ID（如文章ID、评论ID等） */
    @Column
    private Long targetId;

    /** 触发者ID（可为null，如系统通知） */
    @Column
    private Long actorId;

    /** 触发者名称 */
    @Column(length = 30)
    private String actorName;

    /** 是否已读 */
    @Column(nullable = false)
    private Boolean isRead = false;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * 通知类型常量
     */
    public static final String TYPE_LIKE_POST = "LIKE_POST";           // 点赞文章
    public static final String TYPE_LIKE_COMMENT = "LIKE_COMMENT";       // 点赞评论
    public static final String TYPE_COMMENT = "COMMENT";                 // 评论
    public static final String TYPE_REPLY = "REPLY";                     // 回复
    public static final String TYPE_MENTION = "MENTION";                 // @提及
    public static final String TYPE_SYSTEM = "SYSTEM";                   // 系统通知
    public static final String TYPE_FOLLOW = "FOLLOW";                   // 关注
    public static final String TYPE_POST_APPROVAL = "POST_APPROVAL";     // 文章审核结果
    public static final String TYPE_COMMENT_APPROVAL = "COMMENT_APPROVAL"; // 评论审核结果
}