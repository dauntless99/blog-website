package com.blog.service.controller;

import com.blog.common.result.Result;
import com.blog.service.entity.BlogComment;
import com.blog.service.service.CommentService;
import com.blog.service.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评论控制器
 * 处理评论的CRUD和点赞请求
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final LikeService likeService;

    /**
     * 获取文章的评论列表
     * @param postId 文章ID
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认20）
     * @return 评论分页
     */
    @GetMapping
    public Result<Map<String, Object>> getComments(
            @RequestParam Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<BlogComment> commentPage = commentService.getComments(postId, page, size);
        return Result.success(Map.of(
                "content", commentPage.getContent(),
                "totalElements", commentPage.getTotalElements(),
                "totalPages", commentPage.getTotalPages(),
                "currentPage", commentPage.getNumber(),
                "size", commentPage.getSize()
        ));
    }

    /**
     * 获取评论详情
     * @param id 评论ID
     * @return 评论详情
     */
    @GetMapping("/{id}")
    public Result<BlogComment> getCommentDetail(@PathVariable Long id) {
        try {
            return Result.success(commentService.getCommentDetail(id));
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        }
    }

    /**
     * 创建评论
     * @param request 请求体，包含postId、content、parentId（可选）
     * @param token 用户Token（Authorization请求头）
     * @return 创建的评论
     */
    @PostMapping
    public Result<BlogComment> createComment(@RequestBody Map<String, Object> request,
                                              @RequestHeader("Authorization") String token) {
        try {
            return Result.success("评论成功", commentService.createComment(request, token));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新评论
     * @param id 评论ID
     * @param request 请求体，包含content
     * @param token 用户Token（Authorization请求头）
     * @return 更新后的评论
     */
    @PutMapping("/{id}")
    public Result<BlogComment> updateComment(@PathVariable Long id,
                                              @RequestBody Map<String, Object> request,
                                              @RequestHeader("Authorization") String token) {
        try {
            return Result.success("更新成功", commentService.updateComment(id, request, token));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除评论
     * @param id 评论ID
     * @param token 用户Token（Authorization请求头）
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id,
                                       @RequestHeader("Authorization") String token) {
        try {
            commentService.deleteComment(id, token);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 点赞评论
     * @param id 评论ID
     * @param token 用户Token（Authorization请求头）
     * @return 是否点赞成功（true-点赞，false-取消点赞）
     */
    @PostMapping("/{id}/like")
    public Result<Boolean> likeComment(@PathVariable Long id,
                                        @RequestHeader("Authorization") String token) {
        try {
            boolean liked = likeService.likeComment(id, token);
            return Result.success(liked);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查用户是否已点赞评论
     * @param id 评论ID
     * @param token 用户Token（Authorization请求头）
     * @return 是否已点赞
     */
    @GetMapping("/{id}/liked")
    public Result<Boolean> isCommentLiked(@PathVariable Long id,
                                           @RequestHeader("Authorization") String token) {
        try {
            boolean liked = likeService.isCommentLiked(id, token);
            return Result.success(liked);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取评论点赞数
     * @param id 评论ID
     * @return 点赞数
     */
    @GetMapping("/{id}/like-count")
    public Result<Long> getCommentLikeCount(@PathVariable Long id) {
        return Result.success(likeService.getCommentLikeCount(id));
    }

    /**
     * 审核评论（管理员权限）
     * @param id 评论ID
     * @param request 请求体，包含status
     * @param token 用户Token（Authorization请求头）
     * @return 操作结果
     */
    @PostMapping("/{id}/approve")
    public Result<Void> approveComment(@PathVariable Long id,
                                        @RequestBody Map<String, Object> request,
                                        @RequestHeader("Authorization") String token) {
        try {
            String status = (String) request.get("status");
            commentService.approveComment(id, status, token);
            return Result.success("审核成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取待审核评论列表（管理员权限）
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认20）
     * @return 待审核评论分页
     */
    @GetMapping("/pending")
    public Result<Map<String, Object>> getPendingComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<BlogComment> commentPage = commentService.getPendingComments(page, size);
        return Result.success(Map.of(
                "content", commentPage.getContent(),
                "totalElements", commentPage.getTotalElements(),
                "totalPages", commentPage.getTotalPages(),
                "currentPage", commentPage.getNumber(),
                "size", commentPage.getSize()
        ));
    }

    /**
     * 批量审核评论（管理员权限）
     * @param request 请求体，包含commentIds和status
     * @param token 用户Token（Authorization请求头）
     * @return 操作结果
     */
    @PostMapping("/batch-approve")
    public Result<Void> batchApproveComments(@RequestBody Map<String, Object> request,
                                              @RequestHeader("Authorization") String token) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> commentIds = (List<Long>) request.get("commentIds");
            String status = (String) request.get("status");
            commentService.batchApproveComments(commentIds, status, token);
            return Result.success("批量审核成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}