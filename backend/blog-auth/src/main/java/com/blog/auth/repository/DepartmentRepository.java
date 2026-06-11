package com.blog.auth.repository;

import com.blog.auth.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 部门数据访问层
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * 根据上级部门ID查询子部门
     * @param parentId 上级部门ID
     * @return 子部门列表
     */
    List<Department> findByParentIdOrderBySortOrderAsc(Long parentId);

    /**
     * 查询所有启用的部门
     * @return 启用的部门列表
     */
    List<Department> findByEnabledTrueOrderBySortOrderAsc();

    /**
     * 根据部门名称查询
     * @param name 部门名称
     * @return 部门列表
     */
    List<Department> findByNameContaining(String name);
}