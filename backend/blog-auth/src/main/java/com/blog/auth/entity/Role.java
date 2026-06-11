package com.blog.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 角色实体类
 * 定义系统中的角色类型，用于权限控制
 */
@Data
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 角色名称（如：ADMIN, MODERATOR, USER） */
    @Column(unique = true, nullable = false, length = 30)
    private String name;

    /** 角色显示名称 */
    @Column(length = 50)
    private String displayName;

    /** 角色描述 */
    @Column(length = 200)
    private String description;
}