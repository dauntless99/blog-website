package com.blog.forum.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "forum_threads")
public class ForumThread extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Long categoryId;

    @Column(length = 50)
    private String categoryName;

    @Column(nullable = false)
    private Long authorId;

    @Column(length = 30)
    private String authorName;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Integer replyCount = 0;

    @Column(nullable = false)
    private Boolean isPinned = false;

    @Column(nullable = false)
    private Boolean isLocked = false;
}
