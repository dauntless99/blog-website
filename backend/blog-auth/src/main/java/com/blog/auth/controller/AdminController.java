package com.blog.auth.controller;

import com.blog.auth.entity.User;
import com.blog.auth.service.AdminService;
import com.blog.common.result.Result;
import com.blog.forum.entity.ForumThread;
import com.blog.service.entity.BlogComment;
import com.blog.service.entity.BlogPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员控制器
 * 提供管理员控制台、数据统计、内容审核等接口
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

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

    // ==================== 文章审核 ====================

    /**
     * 获取待审核文章列表
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @return 分页待审核文章列表
     */
    @GetMapping("/posts/pending")
    public Result<Map<String, Object>> getPendingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BlogPost> postPage = adminService.getPendingPosts(page, size);
        return Result.success(Map.of(
                "content", postPage.getContent(),
                "totalElements", postPage.getTotalElements(),
                "totalPages", postPage.getTotalPages(),
                "currentPage", postPage.getNumber(),
                "size", postPage.getSize()
        ));
    }

    /**
     * 审核文章
     * @param id 文章ID
     * @param request 审核请求（包含status字段）
     * @return 更新后的文章
     */
    @PutMapping("/posts/{id}/review")
    public Result<BlogPost> reviewPost(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String status = (String) request.get("status");
            return Result.success("审核成功", adminService.reviewPost(id, status));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除文章（管理员权限）
     * @param id 文章ID
     * @return 删除结果
     */
    @DeleteMapping("/posts/{id}")
    public Result<Void> deletePostAsAdmin(@PathVariable Long id) {
        try {
            adminService.deletePostAsAdmin(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ==================== 帖子管理 ====================

    /**
     * 获取帖子列表
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @return 分页帖子列表
     */
    @GetMapping("/threads")
    public Result<Map<String, Object>> getThreadList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ForumThread> threadPage = adminService.getThreadList(page, size);
        return Result.success(Map.of(
                "content", threadPage.getContent(),
                "totalElements", threadPage.getTotalElements(),
                "totalPages", threadPage.getTotalPages(),
                "currentPage", threadPage.getNumber(),
                "size", threadPage.getSize()
        ));
    }

    /**
     * 删除帖子（管理员权限）
     * @param id 帖子ID
     * @return 删除结果
     */
    @DeleteMapping("/threads/{id}")
    public Result<Void> deleteThreadAsAdmin(@PathVariable Long id) {
        try {
            adminService.deleteThreadAsAdmin(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 置顶/取消置顶帖子
     * @param id 帖子ID
     * @param isPinned 是否置顶
     * @return 更新后的帖子
     */
    @PutMapping("/threads/{id}/pin")
    public Result<ForumThread> toggleThreadPin(
            @PathVariable Long id,
            @RequestParam boolean isPinned) {
        try {
            return Result.success(adminService.toggleThreadPin(id, isPinned));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 锁定/解锁帖子
     * @param id 帖子ID
     * @param isLocked 是否锁定
     * @return 更新后的帖子
     */
    @PutMapping("/threads/{id}/lock")
    public Result<ForumThread> toggleThreadLock(
            @PathVariable Long id,
            @RequestParam boolean isLocked) {
        try {
            return Result.success(adminService.toggleThreadLock(id, isLocked));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ==================== 评论管理 ====================

    /**
     * 获取评论列表
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认20）
     * @return 分页评论列表
     */
    @GetMapping("/comments")
    public Result<Map<String, Object>> getCommentList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<BlogComment> commentPage = adminService.getCommentList(page, size);
        return Result.success(Map.of(
                "content", commentPage.getContent(),
                "totalElements", commentPage.getTotalElements(),
                "totalPages", commentPage.getTotalPages(),
                "currentPage", commentPage.getNumber(),
                "size", commentPage.getSize()
        ));
    }

    /**
     * 删除评论（管理员权限）
     * @param id 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteCommentAsAdmin(@PathVariable Long id) {
        try {
            adminService.deleteCommentAsAdmin(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}