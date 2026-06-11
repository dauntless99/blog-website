package com.blog.service.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客评论实体类
 * 对应数据库表: blog_comments
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blog_comments")
public class BlogComment extends BaseEntity {

    /** 所属文章ID */
    @Column(nullable = false)
    private Long postId;

    /** 评论内容 */
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    /** 作者ID */
    @Column(nullable = false)
    private Long authorId;

    /** 作者名称（冗余字段） */
    @Column(length = 30)
    private String authorName;

    /** 作者头像（冗余字段） */
    @Column(length = 500)
    private String authorAvatar;

    /** 父评论ID（用于回复评论，可为null） */
    @Column(nullable = true)
    private Long parentId;

    /** 点赞数 */
    @Column(nullable = false)
    private Integer likeCount = 0;

    /** 评论状态：approved-已审核, pending-待审核, rejected-已拒绝 */
    @Column(nullable = false, length = 20)
    private String status = "approved";

    /** 是否是管理员回复 */
    @Column(nullable = false)
    private Boolean isAdminReply = false;
}