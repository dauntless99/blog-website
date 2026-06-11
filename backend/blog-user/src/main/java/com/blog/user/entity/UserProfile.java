package com.blog.user.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户资料实体类
 * 对应数据库表: user_profiles
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {

    /** 用户ID（唯一） */
    @Column(nullable = false, unique = true)
    private Long userId;

    /** 用户昵称 */
    @Column(length = 30)
    private String nickname;

    /** 头像URL */
    @Column(length = 500)
    private String avatar;

    /** 个人简介 */
    @Column(length = 200)
    private String bio;

    /** 个人网站 */
    @Column(length = 100)
    private String website;

    /** 所在地 */
    @Column(length = 50)
    private String location;

    /** 文章数量 */
    @Column(nullable = false)
    private Integer postCount = 0;

    /** 帖子数量 */
    @Column(nullable = false)
    private Integer threadCount = 0;

    /** 回复数量 */
    @Column(nullable = false)
    private Integer replyCount = 0;
}
