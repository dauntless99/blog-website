package com.blog.forum.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 论坛回复实体类
 * 对应数据库表: forum_replies
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "forum_replies")
public class ForumReply extends BaseEntity {

    /** 回复内容 */
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    /** 所属帖子ID */
    @Column(nullable = false)
    private Long threadId;

    /** 作者ID */
    @Column(nullable = false)
    private Long authorId;

    /** 作者名称（冗余字段） */
    @Column(length = 30)
    private String authorName;

    /** 父回复ID（用于盖楼回复，可为null） */
    @Column(nullable = true)
    private Long parentId;
}
