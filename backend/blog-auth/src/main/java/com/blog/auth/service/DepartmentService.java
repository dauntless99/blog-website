package com.blog.auth.service;

import com.blog.auth.entity.Department;
import com.blog.auth.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 部门服务类
 * 提供部门管理相关业务逻辑
 */
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    /**
     * 获取所有部门（树形结构）
     * @return 部门树形列表
     */
    public List<Map<String, Object>> getAllDepartmentsTree() {
        List<Department> departments = departmentRepository.findByEnabledTrueOrderBySortOrderAsc();
        return buildDepartmentTree(departments, null);
    }

    /**
     * 递归构建部门树
     * @param departments 所有部门列表
     * @param parentId 上级部门ID
     * @return 子部门树形列表
     */
    private List<Map<String, Object>> buildDepartmentTree(List<Department> departments, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Department dept : departments) {
            if ((parentId == null && dept.getParentId() == null) || 
                (parentId != null && parentId.equals(dept.getParentId()))) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", dept.getId());
                node.put("name", dept.getName());
                node.put("description", dept.getDescription());
                node.put("parentId", dept.getParentId());
                node.put("managerId", dept.getManagerId());
                node.put("sortOrder", dept.getSortOrder());
                node.put("enabled", dept.getEnabled());
                // 递归获取子部门
                List<Map<String, Object>> children = buildDepartmentTree(departments, dept.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }
                tree.add(node);
            }
        }
        return tree;
    }

    /**
     * 获取部门列表（平面结构）
     * @return 部门列表
     */
    public List<Department> getAllDepartmentsFlat() {
        return departmentRepository.findByEnabledTrueOrderBySortOrderAsc();
    }

    /**
     * 根据ID获取部门详情
     * @param id 部门ID
     * @return 部门详情
     * @throws RuntimeException 部门不存在时抛出异常
     */
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
    }

    /**
     * 创建部门
     * @param department 部门对象
     * @return 创建的部门
     */
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    /**
     * 更新部门
     * @param id 部门ID
     * @param department 更新的部门信息
     * @return 更新后的部门
     * @throws RuntimeException 部门不存在时抛出异常
     */
    public Department updateDepartment(Long id, Department department) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
        existing.setName(department.getName());
        existing.setDescription(department.getDescription());
        existing.setParentId(department.getParentId());
        existing.setManagerId(department.getManagerId());
        existing.setSortOrder(department.getSortOrder());
        existing.setEnabled(department.getEnabled());
        return departmentRepository.save(existing);
    }

    /**
     * 删除部门（逻辑删除，设置为禁用）
     * @param id 部门ID
     * @throws RuntimeException 部门不存在时抛出异常
     */
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
        department.setEnabled(false);
        departmentRepository.save(department);
    }

    /**
     * 搜索部门
     * @param keyword 搜索关键词（部门名称）
     * @return 匹配的部门列表
     */
    public List<Department> searchDepartments(String keyword) {
        return departmentRepository.findByNameContaining(keyword);
    }
}