package com.blog.auth.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体类
 * 对应数据库表: departments
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "departments")
public class Department extends BaseEntity {
    /** 部门名称 */
    @Column(name = "dept_name", nullable = false, length = 100)
    private String deptName;

    /** 部门描述 */
    @Column(length = 500)
    private String description;

    /** 上级部门ID（用于构建部门树） */
    @Column
    private Long parentId;

    /** 部门负责人ID */
    @Column
    private Long managerId;

    /** 排序字段 */
    @Column(nullable = false)
    private Integer sortOrder = 0;

    /** 是否启用 */
    @Column(nullable = false)
    private Boolean enabled = true;
}
