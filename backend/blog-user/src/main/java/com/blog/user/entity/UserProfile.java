package com.blog.user.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(length = 30)
    private String nickname;

    @Column(length = 500)
    private String avatar;

    @Column(length = 200)
    private String bio;

    @Column(length = 100)
    private String website;

    @Column(length = 50)
    private String location;

    @Column(nullable = false)
    private Integer postCount = 0;

    @Column(nullable = false)
    private Integer threadCount = 0;

    @Column(nullable = false)
    private Integer replyCount = 0;
}
