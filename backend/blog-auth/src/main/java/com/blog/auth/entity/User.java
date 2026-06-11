package com.blog.auth.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 * 对应数据库表: users
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    /** 用户名（唯一标识，用于登录） */
    @Column(unique = true, nullable = false, length = 20)
    private String username;

    /** 密码（BCrypt加密存储） */
    @Column(nullable = false)
    private String password;

    /** 邮箱地址 */
    @Column(length = 100)
    private String email;

    /** 用户昵称（显示用） */
    @Column(length = 30)
    private String nickname;

    /** 头像URL */
    @Column(length = 500)
    private String avatar;

    /** 个人简介 */
    @Column(length = 200)
    private String bio;

    /** 用户角色：0-普通用户，1-管理员 */
    @Column(nullable = false)
    private Integer role = 0;
}
