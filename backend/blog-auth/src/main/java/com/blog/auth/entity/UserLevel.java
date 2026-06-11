package com.blog.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 用户等级实体类
 * 定义用户等级体系
 */
@Data
@Entity
@Table(name = "user_levels")
public class UserLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 等级名称 */
    @Column(nullable = false, length = 30)
    private String name;

    /** 等级图标 */
    @Column(length = 100)
    private String icon;

    /** 所需最低积分 */
    @Column(nullable = false)
    private Integer minPoints;

    /** 等级颜色 */
    @Column(length = 20)
    private String color;

    /** 等级特权描述 */
    @Column(length = 500)
    private String privileges;
}