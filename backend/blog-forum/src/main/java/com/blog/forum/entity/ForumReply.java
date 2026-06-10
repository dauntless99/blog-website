package com.blog.forum.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "forum_replies")
public class ForumReply extends BaseEntity {

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Long threadId;

    @Column(nullable = false)
    private Long authorId;

    @Column(length = 30)
    private String authorName;

    @Column(nullable = true)
    private Long parentId; // 回复的回复 (盖楼)
}
