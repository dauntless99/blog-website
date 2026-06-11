package com.blog.auth.controller;

import com.blog.auth.entity.Announcement;
import com.blog.auth.service.AnnouncementService;
import com.blog.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公告控制器
 * 处理公告管理相关请求
 */
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 获取已发布的公告列表
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @return 分页公告列表
     */
    @GetMapping
    public Result<Map<String, Object>> getPublishedAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Announcement> announcementPage = announcementService.getPublishedAnnouncements(page, size);
        return Result.success(Map.of(
                "content", announcementPage.getContent(),
                "totalElements", announcementPage.getTotalElements(),
                "totalPages", announcementPage.getTotalPages(),
                "currentPage", announcementPage.getNumber(),
                "size", announcementPage.getSize()
        ));
    }

    /**
     * 获取置顶公告
     * @return 置顶公告列表
     */
    @GetMapping("/pinned")
    public Result<List<Announcement>> getPinnedAnnouncements() {
        return Result.success(announcementService.getPinnedAnnouncements());
    }

    /**
     * 获取公告详情
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncement(@PathVariable Long id) {
        try {
            return Result.success(announcementService.getAnnouncementById(id));
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        }
    }

    /**
     * 创建公告（草稿状态）
     * @param request 创建请求体
     * @param token 用户Token（Authorization请求头）
     * @return 创建的公告
     */
    @PostMapping
    public Result<Announcement> createAnnouncement(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        try {
            return Result.success("创建成功", announcementService.createAnnouncement(request, token));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发布公告
     * @param id 公告ID
     * @param token 用户Token（Authorization请求头）
     * @return 发布后的公告
     */
    @PutMapping("/{id}/publish")
    public Result<Announcement> publishAnnouncement(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            return Result.success("发布成功", announcementService.publishAnnouncement(id, token));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新公告
     * @param id 公告ID
     * @param request 更新请求体
     * @return 更新后的公告
     */
    @PutMapping("/{id}")
    public Result<Announcement> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            return Result.success("更新成功", announcementService.updateAnnouncement(id, request));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 归档公告
     * @param id 公告ID
     * @return 归档后的公告
     */
    @PutMapping("/{id}/archive")
    public Result<Announcement> archiveAnnouncement(@PathVariable Long id) {
        try {
            return Result.success("归档成功", announcementService.archiveAnnouncement(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除公告
     * @param id 公告ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        try {
            announcementService.deleteAnnouncement(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}