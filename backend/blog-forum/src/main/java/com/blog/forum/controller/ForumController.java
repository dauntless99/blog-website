package com.blog.forum.controller;

import com.blog.common.result.Result;
import com.blog.forum.entity.ForumCategory;
import com.blog.forum.entity.ForumReply;
import com.blog.forum.entity.ForumThread;
import com.blog.forum.service.ForumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 论坛控制器
 * 处理分类管理、帖子管理、回复管理等请求
 */
@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
public class ForumController {

    /** 论坛服务 */
    private final ForumService forumService;

    // ==================== 分类相关接口 ====================

    /**
     * 获取所有分类
     * @return 分类列表
     */
    @GetMapping("/categories")
    public Result<List<ForumCategory>> getCategories() {
        return Result.success(forumService.getAllCategories());
    }

    /**
     * 创建分类
     * @param category 分类对象
     * @return 创建的分类
     */
    @PostMapping("/categories")
    public Result<ForumCategory> createCategory(@RequestBody ForumCategory category) {
        return Result.success("创建分类成功", forumService.createCategory(category));
    }

    // ==================== 帖子相关接口 ====================

    /**
     * 获取帖子列表
     * @param categoryId 分类ID（可选）
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @param keyword 搜索关键词（可选）
     * @return 分页帖子列表
     */
    @GetMapping("/threads")
    public Result<Map<String, Object>> getThreads(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<ForumThread> threadPage = forumService.getThreads(categoryId, page, size, keyword);
        return Result.success(Map.of(
                "content", threadPage.getContent(),
                "totalElements", threadPage.getTotalElements(),
                "totalPages", threadPage.getTotalPages(),
                "currentPage", threadPage.getNumber(),
                "size", threadPage.getSize()
        ));
    }

    /**
     * 获取帖子详情
     * @param id 帖子ID
     * @return 帖子详情
     */
    @GetMapping("/threads/{id}")
    public Result<ForumThread> getThreadDetail(@PathVariable Long id) {
        try {
            return Result.success(forumService.getThreadDetail(id));
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        }
    }

    /**
     * 创建帖子
     * @param request 创建请求体
     * @param token 用户Token（Authorization请求头）
     * @return 创建的帖子
     */
    @PostMapping("/threads")
    public Result<ForumThread> createThread(@RequestBody Map<String, Object> request,
                                             @RequestHeader("Authorization") String token) {
        try {
            return Result.success("发帖成功", forumService.createThread(request, token));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除帖子
     * @param id 帖子ID
     * @param token 用户Token（Authorization请求头）
     * @return 删除结果
     */
    @DeleteMapping("/threads/{id}")
    public Result<Void> deleteThread(@PathVariable Long id,
                                      @RequestHeader("Authorization") String token) {
        try {
            forumService.deleteThread(id, token);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ==================== 回复相关接口 ====================

    /**
     * 获取帖子回复列表
     * @param threadId 帖子ID
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认20）
     * @return 分页回复列表
     */
    @GetMapping("/threads/{threadId}/replies")
    public Result<Map<String, Object>> getReplies(
            @PathVariable Long threadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ForumReply> replyPage = forumService.getReplies(threadId, page, size);
        return Result.success(Map.of(
                "content", replyPage.getContent(),
                "totalElements", replyPage.getTotalElements(),
                "totalPages", replyPage.getTotalPages(),
                "currentPage", replyPage.getNumber(),
                "size", replyPage.getSize()
        ));
    }

    /**
     * 创建回复
     * @param request 创建请求体
     * @param token 用户Token（Authorization请求头）
     * @return 创建的回复
     */
    @PostMapping("/replies")
    public Result<ForumReply> createReply(@RequestBody Map<String, Object> request,
                                           @RequestHeader("Authorization") String token) {
        try {
            return Result.success("回复成功", forumService.createReply(request, token));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
