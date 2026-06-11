package com.blog.forum.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 论坛帖子实体类
 * 对应数据库表: forum_threads
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "forum_threads")
public class ForumThread extends BaseEntity {

    /** 帖子标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 帖子内容 */
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    /** 分类ID */
    @Column(nullable = false)
    private Long categoryId;

    /** 分类名称（冗余字段，避免关联查询） */
    @Column(length = 50)
    private String categoryName;

    /** 作者ID */
    @Column(nullable = false)
    private Long authorId;

    /** 作者名称（冗余字段） */
    @Column(length = 30)
    private String authorName;

    /** 阅读量 */
    @Column(nullable = false)
    private Integer viewCount = 0;

    /** 回复数 */
    @Column(nullable = false)
    private Integer replyCount = 0;

    /** 是否置顶 */
    @Column(nullable = false)
    private Boolean isPinned = false;

    /** 是否锁定（锁定后无法回复） */
    @Column(nullable = false)
    private Boolean isLocked = false;
}
