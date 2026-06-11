package com.blog.auth.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 积分记录实体类
 * 记录用户积分的增减变化
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "point_records")
public class PointRecord extends BaseEntity {

    /** 用户ID */
    @Column(nullable = false)
    private Long userId;

    /** 积分变化值（正数增加，负数减少） */
    @Column(nullable = false)
    private Integer points;

    /** 变动类型（如：POST_CREATE, COMMENT, LIKE, LOGIN等） */
    @Column(nullable = false, length = 50)
    private String type;

    /** 相关业务ID（如文章ID、评论ID） */
    @Column
    private Long targetId;

    /** 变动描述 */
    @Column(length = 200)
    private String description;

    /** 变动前积分 */
    @Column(nullable = false)
    private Integer beforePoints;

    /** 变动后积分 */
    @Column(nullable = false)
    private Integer afterPoints;
}