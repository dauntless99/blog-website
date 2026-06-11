package com.blog.common.controller;

import com.blog.common.entity.Notification;
import com.blog.common.service.NotificationService;
import com.blog.common.result.Result;
import com.blog.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通知控制器
 * 处理通知的查询、标记已读等请求
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取通知列表
     * @param token 用户Token
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认20）
     * @return 通知分页
     */
    @GetMapping
    public Result<Map<String, Object>> getNotifications(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = JwtUtil.getUserId(token);
        Page<Notification> notificationPage = notificationService.getNotifications(userId, page, size);
        return Result.success(Map.of(
                "content", notificationPage.getContent(),
                "totalElements", notificationPage.getTotalElements(),
                "totalPages", notificationPage.getTotalPages(),
                "currentPage", notificationPage.getNumber(),
                "size", notificationPage.getSize()
        ));
    }

    /**
     * 获取未读通知数量
     * @param token 用户Token
     * @return 未读通知数
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 标记单条通知为已读
     * @param id 通知ID
     * @param token 用户Token
     * @return 操作结果
     */
    @PostMapping("/{id}/mark-read")
    public Result<Void> markAsRead(@PathVariable Long id,
                                    @RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        notificationService.markAsRead(userId, id);
        return Result.success("标记成功", null);
    }

    /**
     * 标记所有通知为已读
     * @param token 用户Token
     * @return 操作结果
     */
    @PostMapping("/mark-all-read")
    public Result<Void> markAllAsRead(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        notificationService.markAllAsRead(userId);
        return Result.success("全部标记为已读", null);
    }

    /**
     * 删除单条通知
     * @param id 通知ID
     * @param token 用户Token
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(@PathVariable Long id,
                                           @RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        notificationService.deleteNotification(userId, id);
        return Result.success("删除成功", null);
    }
}