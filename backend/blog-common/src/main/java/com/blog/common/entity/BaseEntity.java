package com.blog.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 基础实体类
 * 所有实体类的基类，提供通用字段和审计功能
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /** 主键ID，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 创建时间（自动生成，不可更新） */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间（自动更新） */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
