package com.blog.auth.controller;

import com.blog.auth.entity.Department;
import com.blog.auth.service.DepartmentService;
import com.blog.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 部门控制器
 * 处理部门管理相关请求
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 获取部门树结构
     * @return 部门树形列表
     */
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> getDepartmentTree() {
        return Result.success(departmentService.getAllDepartmentsTree());
    }

    /**
     * 获取部门列表（平面结构）
     * @return 部门列表
     */
    @GetMapping
    public Result<List<Department>> getDepartments() {
        return Result.success(departmentService.getAllDepartmentsFlat());
    }

    /**
     * 获取部门详情
     * @param id 部门ID
     * @return 部门详情
     */
    @GetMapping("/{id}")
    public Result<Department> getDepartment(@PathVariable Long id) {
        try {
            return Result.success(departmentService.getDepartmentById(id));
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        }
    }

    /**
     * 创建部门
     * @param department 部门对象
     * @return 创建的部门
     */
    @PostMapping
    public Result<Department> createDepartment(@RequestBody Department department) {
        return Result.success("创建成功", departmentService.createDepartment(department));
    }

    /**
     * 更新部门
     * @param id 部门ID
     * @param department 更新的部门信息
     * @return 更新后的部门
     */
    @PutMapping("/{id}")
    public Result<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        try {
            return Result.success("更新成功", departmentService.updateDepartment(id, department));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除部门（逻辑删除）
     * @param id 部门ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 搜索部门
     * @param keyword 搜索关键词
     * @return 匹配的部门列表
     */
    @GetMapping("/search")
    public Result<List<Department>> searchDepartments(@RequestParam String keyword) {
        return Result.success(departmentService.searchDepartments(keyword));
    }
}