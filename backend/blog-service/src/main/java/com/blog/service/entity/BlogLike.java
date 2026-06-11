package com.blog.service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 博客点赞实体类
 * 对应数据库表: blog_likes
 */
@Data
@Entity
@Table(name = "blog_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"postId", "userId"})
})
public class BlogLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 文章ID */
    @Column(nullable = false)
    private Long postId;

    /** 用户ID */
    @Column(nullable = false)
    private Long userId;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 点赞类型：POST-文章点赞, COMMENT-评论点赞 */
    @Column(nullable = false, length = 20)
    private String type = "POST";
}