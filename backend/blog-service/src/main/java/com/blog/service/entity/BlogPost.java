package com.blog.service.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客文章实体类
 * 对应数据库表: blog_posts
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blog_posts")
public class BlogPost extends BaseEntity {

    /** 文章标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 文章内容（支持Markdown） */
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    /** 文章摘要/简介 */
    @Column(length = 500)
    private String summary;

    /** 作者ID */
    @Column(nullable = false)
    private Long authorId;

    /** 作者名称 */
    @Column(length = 30)
    private String authorName;

    /** 文章分类 */
    @Column(length = 50)
    private String category;

    /** 文章标签（逗号分隔） */
    @Column(length = 200)
    private String tags;

    /** 阅读量 */
    @Column(nullable = false)
    private Integer viewCount = 0;

    /** 点赞数 */
    @Column(nullable = false)
    private Integer likeCount = 0;

    /** 评论数 */
    @Column(nullable = false)
    private Integer commentCount = 0;

    /** 文章状态：draft-草稿，published-已发布 */
    @Column(nullable = false, length = 20)
    private String status = "published";
}
