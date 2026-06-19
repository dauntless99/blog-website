package com.blog.auth.controller;

import com.blog.auth.entity.Announcement;
import com.blog.auth.entity.Department;
import com.blog.auth.service.AdminService;
import com.blog.auth.service.AnnouncementService;
import com.blog.auth.service.DepartmentService;
import com.blog.common.entity.Role;
import com.blog.common.entity.User;
import com.blog.common.entity.UserLevel;
import com.blog.common.service.PermissionService;
import com.blog.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员控制器
 * 提供管理员控制台、数据统计、用户管理等接口
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final DepartmentService departmentService;
    private final AnnouncementService announcementService;
    private final PermissionService permissionService;

    /**
     * 获取系统统计数据
     * @return 统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getSystemStats() {
        return Result.success(adminService.getSystemStats());
    }

    // ==================== 用户管理 ====================

    /**
     * 获取用户列表
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @return 分页用户列表
     */
    @GetMapping("/users")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = adminService.getUserList(page, size);
        return Result.success(Map.of(
                "content", userPage.getContent(),
                "totalElements", userPage.getTotalElements(),
                "totalPages", userPage.getTotalPages(),
                "currentPage", userPage.getNumber(),
                "size", userPage.getSize()
        ));
    }

    // ==================== 用户等级管理 ====================

    /**
     * 获取用户等级列表
     * @param page 页码
     * @param size 每页大小
     * @return 用户等级列表
     */
    @GetMapping("/user-levels")
    public Result<Map<String, Object>> getUserLevelList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserLevel> levelPage = adminService.getUserLevelList(page, size);
        return Result.success(Map.of(
                "content", levelPage.getContent(),
                "totalElements", levelPage.getTotalElements(),
                "totalPages", levelPage.getTotalPages(),
                "currentPage", levelPage.getNumber(),
                "size", levelPage.getSize()
        ));
    }

    // ==================== 角色管理 ====================

    /**
     * 获取角色列表
     * @param page 页码
     * @param size 每页大小
     * @return 角色列表
     */
    @GetMapping("/roles")
    public Result<Map<String, Object>> getRoleList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Role> rolePage = adminService.getRoleList(page, size);
        return Result.success(Map.of(
                "content", rolePage.getContent(),
                "totalElements", rolePage.getTotalElements(),
                "totalPages", rolePage.getTotalPages(),
                "currentPage", rolePage.getNumber(),
                "size", rolePage.getSize()
        ));
    }

    /**
     * 创建角色
     * @param request 角色信息
     * @return 创建的角色
     */
    @PostMapping("/roles")
    public Result<Role> createRole(@RequestBody Map<String, Object> request) {
        try {
            Role role = new Role();
            role.setRoleName((String) request.get("roleName"));
            role.setRoleKey((String) request.get("roleKey"));
            role.setDescription((String) request.get("description"));
            return Result.success("创建成功", permissionService.createRole(role));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新角色
     * @param id 角色ID
     * @param request 更新信息
     * @return 更新后的角色
     */
    @PutMapping("/roles/{id}")
    public Result<Role> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Role role = permissionService.getRoleById(id);
            if (request.containsKey("roleName")) {
                role.setRoleName((String) request.get("roleName"));
            }
            if (request.containsKey("roleKey")) {
                role.setRoleKey((String) request.get("roleKey"));
            }
            if (request.containsKey("description")) {
                role.setDescription((String) request.get("description"));
            }
            return Result.success("更新成功", permissionService.updateRole(role));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除角色
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/roles/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        try {
            permissionService.deleteRole(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ==================== 部门管理 ====================

    /**
     * 获取部门列表
     * @param page 页码
     * @param size 每页大小
     * @return 部门列表
     */
    @GetMapping("/departments")
    public Result<Map<String, Object>> getDepartmentList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Department> deptPage = adminService.getDepartmentList(page, size);
        return Result.success(Map.of(
                "content", deptPage.getContent(),
                "totalElements", deptPage.getTotalElements(),
                "totalPages", deptPage.getTotalPages(),
                "currentPage", deptPage.getNumber(),
                "size", deptPage.getSize()
        ));
    }

    /**
     * 创建部门
     * @param request 部门信息
     * @return 创建的部门
     */
    @PostMapping("/departments")
    public Result<Department> createDepartment(@RequestBody Map<String, Object> request) {
        try {
            Department dept = new Department();
            dept.setDeptName((String) request.get("deptName"));
            dept.setDescription((String) request.get("description"));
            return Result.success("创建成功", departmentService.createDepartment(dept));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新部门
     * @param id 部门ID
     * @param request 更新信息
     * @return 更新后的部门
     */
    @PutMapping("/departments/{id}")
    public Result<Department> updateDepartment(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Department dept = departmentService.getDepartmentById(id);
            if (request.containsKey("deptName")) {
                dept.setDeptName((String) request.get("deptName"));
            }
            if (request.containsKey("description")) {
                dept.setDescription((String) request.get("description"));
            }
            return Result.success("更新成功", departmentService.updateDepartment(dept));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除部门
     * @param id 部门ID
     * @return 删除结果
     */
    @DeleteMapping("/departments/{id}")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ==================== 公告管理 ====================

    /**
     * 获取公告列表
     * @param page 页码
     * @param size 每页大小
     * @return 公告列表
     */
    @GetMapping("/announcements")
    public Result<Map<String, Object>> getAnnouncementList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Announcement> announcementPage = adminService.getAnnouncementList(page, size);
        return Result.success(Map.of(
                "content", announcementPage.getContent(),
                "totalElements", announcementPage.getTotalElements(),
                "totalPages", announcementPage.getTotalPages(),
                "currentPage", announcementPage.getNumber(),
                "size", announcementPage.getSize()
        ));
    }

    /**
     * 创建公告
     * @param request 公告信息
     * @return 创建的公告
     */
    @PostMapping("/announcements")
    public Result<Announcement> createAnnouncement(@RequestBody Map<String, Object> request) {
        try {
            Announcement announcement = new Announcement();
            announcement.setTitle((String) request.get("title"));
            announcement.setContent((String) request.get("content"));
            return Result.success("创建成功", announcementService.createAnnouncement(announcement));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新公告
     * @param id 公告ID
     * @param request 更新信息
     * @return 更新后的公告
     */
    @PutMapping("/announcements/{id}")
    public Result<Announcement> updateAnnouncement(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Announcement announcement = announcementService.getAnnouncementById(id);
            if (request.containsKey("title")) {
                announcement.setTitle((String) request.get("title"));
            }
            if (request.containsKey("content")) {
                announcement.setContent((String) request.get("content"));
            }
            if (request.containsKey("status")) {
                announcement.setStatus(String.valueOf(request.get("status")));
            }
            return Result.success("更新成功", announcementService.updateAnnouncement(announcement));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除公告
     * @param id 公告ID
     * @return 删除结果
     */
    @DeleteMapping("/announcements/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        try {
            announcementService.deleteAnnouncement(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}