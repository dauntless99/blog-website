package com.blog.auth.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String email;

    @Column(length = 30)
    private String nickname;

    @Column(length = 500)
    private String avatar;

    @Column(length = 200)
    private String bio;

    @Column(nullable = false)
    private Integer role = 0; // 0:普通用户 1:管理员
}
