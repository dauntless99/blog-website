package com.blog.auth.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 公告实体类
 * 对应数据库表: announcements
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "announcements")
public class Announcement extends BaseEntity {

    /** 公告标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 公告内容 */
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    /** 发布者ID */
    @Column(nullable = false)
    private Long publisherId;

    /** 发布者名称 */
    @Column(length = 30)
    private String publisherName;

    /** 是否置顶 */
    @Column(nullable = false)
    private Boolean isPinned = false;

    /** 状态：draft-草稿，published-已发布，archived-已归档 */
    @Column(nullable = false, length = 20)
    private String status = "draft";

    /** 发布时间 */
    @Column
    private LocalDateTime publishTime;

    /** 截止时间（可选） */
    @Column
    private LocalDateTime expireTime;
}