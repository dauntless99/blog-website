package com.blog.forum.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "forum_categories")
public class ForumCategory extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Integer threadCount = 0;
}
