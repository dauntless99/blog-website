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

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    // ===== 分类 =====
    @GetMapping("/categories")
    public Result<List<ForumCategory>> getCategories() {
        return Result.success(forumService.getAllCategories());
    }

    @PostMapping("/categories")
    public Result<ForumCategory> createCategory(@RequestBody ForumCategory category) {
        return Result.success("创建分类成功", forumService.createCategory(category));
    }

    // ===== 帖子 =====
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

    @GetMapping("/threads/{id}")
    public Result<ForumThread> getThreadDetail(@PathVariable Long id) {
        try {
            return Result.success(forumService.getThreadDetail(id));
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        }
    }

    @PostMapping("/threads")
    public Result<ForumThread> createThread(@RequestBody Map<String, Object> request,
                                             @RequestHeader("Authorization") String token) {
        try {
            return Result.success("发帖成功", forumService.createThread(request, token));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

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

    // ===== 回复 =====
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
