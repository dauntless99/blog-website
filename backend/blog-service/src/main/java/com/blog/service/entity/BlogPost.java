package com.blog.service.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blog_posts")
public class BlogPost extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(length = 500)
    private String summary;

    @Column(nullable = false)
    private Long authorId;

    @Column(length = 30)
    private String authorName;

    @Column(length = 50)
    private String category;

    @Column(length = 200)
    private String tags;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Integer commentCount = 0;

    @Column(nullable = false, length = 20)
    private String status = "published"; // draft, published
}
