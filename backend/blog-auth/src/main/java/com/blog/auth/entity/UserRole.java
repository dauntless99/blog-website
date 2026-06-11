package com.blog.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 用户角色关联实体
 * 建立用户与角色的多对多关系
 */
@Data
@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID */
    @Column(nullable = false)
    private Long userId;

    /** 角色ID */
    @Column(nullable = false)
    private Long roleId;
}