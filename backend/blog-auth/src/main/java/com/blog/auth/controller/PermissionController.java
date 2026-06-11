package com.blog.auth.controller;

import com.blog.auth.entity.UserLevel;
import com.blog.auth.service.PermissionService;
import com.blog.auth.service.PointService;
import com.blog.common.result.Result;
import com.blog.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限与积分控制器
 * 处理角色管理、积分查询等请求
 */
@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final PointService pointService;

    /**
     * 获取当前用户的角色列表
     * @param token 用户Token
     * @return 角色列表
     */
    @GetMapping("/roles")
    public Result<Set<String>> getUserRoles(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        Set<String> roles = permissionService.getUserRoles(userId);
        return Result.success(roles);
    }

    /**
     * 检查当前用户是否是管理员
     * @param token 用户Token
     * @return 是否是管理员
     */
    @GetMapping("/is-admin")
    public Result<Boolean> isAdmin(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        boolean isAdmin = permissionService.isAdmin(userId);
        return Result.success(isAdmin);
    }

    /**
     * 获取当前用户的积分
     * @param token 用户Token
     * @return 当前积分
     */
    @GetMapping("/points")
    public Result<Integer> getUserPoints(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        int points = pointService.getUserPoints(userId);
        return Result.success(points);
    }

    /**
     * 获取当前用户的等级
     * @param token 用户Token
     * @return 用户等级
     */
    @GetMapping("/level")
    public Result<UserLevel> getUserLevel(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        int points = pointService.getUserPoints(userId);
        UserLevel level = permissionService.getUserLevel(points);
        return Result.success(level);
    }

    /**
     * 获取所有等级列表
     * @return 等级列表
     */
    @GetMapping("/levels")
    public Result<List<UserLevel>> getAllLevels() {
        List<UserLevel> levels = permissionService.getAllLevels();
        return Result.success(levels);
    }

    /**
     * 获取用户积分记录
     * @param token 用户Token
     * @param page 页码
     * @param size 每页大小
     * @return 积分记录分页
     */
    @GetMapping("/point-records")
    public Result<Map<String, Object>> getPointRecords(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = JwtUtil.getUserId(token);
        Page<com.blog.auth.entity.PointRecord> recordPage = pointService.getPointRecords(userId, page, size);
        return Result.success(Map.of(
                "content", recordPage.getContent(),
                "totalElements", recordPage.getTotalElements(),
                "totalPages", recordPage.getTotalPages(),
                "currentPage", recordPage.getNumber(),
                "size", recordPage.getSize()
        ));
    }

    /**
     * 管理员为用户分配角色（管理员权限）
     * @param request 请求体，包含userId和roleName
     * @param token 用户Token
     * @return 操作结果
     */
    @PostMapping("/assign-role")
    public Result<Void> assignRole(@RequestBody Map<String, Object> request,
                                   @RequestHeader("Authorization") String token) {
        try {
            Long adminId = JwtUtil.getUserId(token);
            if (!permissionService.isAdmin(adminId)) {
                return Result.forbidden("无权限执行此操作");
            }
            Long userId = Long.valueOf(request.get("userId").toString());
            String roleName = (String) request.get("roleName");
            permissionService.assignRole(userId, roleName);
            return Result.success("角色分配成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员移除用户角色（管理员权限）
     * @param request 请求体，包含userId和roleName
     * @param token 用户Token
     * @return 操作结果
     */
    @PostMapping("/remove-role")
    public Result<Void> removeRole(@RequestBody Map<String, Object> request,
                                   @RequestHeader("Authorization") String token) {
        try {
            Long adminId = JwtUtil.getUserId(token);
            if (!permissionService.isAdmin(adminId)) {
                return Result.forbidden("无权限执行此操作");
            }
            Long userId = Long.valueOf(request.get("userId").toString());
            String roleName = (String) request.get("roleName");
            permissionService.removeRole(userId, roleName);
            return Result.success("角色移除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员调整用户积分（管理员权限）
     * @param request 请求体，包含userId、points和description
     * @param token 用户Token
     * @return 更新后的积分
     */
    @PostMapping("/adjust-points")
    public Result<Integer> adjustPoints(@RequestBody Map<String, Object> request,
                                        @RequestHeader("Authorization") String token) {
        try {
            Long adminId = JwtUtil.getUserId(token);
            if (!permissionService.isAdmin(adminId)) {
                return Result.forbidden("无权限执行此操作");
            }
            Long userId = Long.valueOf(request.get("userId").toString());
            int points = Integer.parseInt(request.get("points").toString());
            String description = (String) request.getOrDefault("description", "系统调整");
            int newPoints = pointService.adjustPoints(userId, points, description);
            return Result.success("积分调整成功", newPoints);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}