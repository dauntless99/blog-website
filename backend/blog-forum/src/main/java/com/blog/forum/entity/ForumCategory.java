package com.blog.forum.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 论坛分类实体类
 * 对应数据库表: forum_categories
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "forum_categories")
public class ForumCategory extends BaseEntity {

    /** 分类名称 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 分类描述 */
    @Column(length = 200)
    private String description;

    /** 排序顺序（数字越小越靠前） */
    @Column(nullable = false)
    private Integer sortOrder = 0;

    /** 帖子数量 */
    @Column(nullable = false)
    private Integer threadCount = 0;
}
