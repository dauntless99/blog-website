package com.blog.service.controller;

import com.blog.common.result.Result;
import com.blog.service.entity.BlogPost;
import com.blog.service.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 博客文章控制器
 * 处理文章列表、详情、创建、更新、删除等请求
 */
@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {

    /** 博客服务 */
    private final BlogService blogService;

    /**
     * 获取文章列表
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @param category 分类筛选（可选）
     * @param keyword 关键词搜索（可选）
     * @return 分页文章列表
     */
    @GetMapping("/posts")
    public Result<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        Page<BlogPost> postPage = blogService.getPostList(page, size, category, keyword);
        return Result.success(Map.of(
                "content", postPage.getContent(),
                "totalElements", postPage.getTotalElements(),
                "totalPages", postPage.getTotalPages(),
                "currentPage", postPage.getNumber(),
                "size", postPage.getSize()
        ));
    }

    /**
     * 获取文章详情
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/posts/{id}")
    public Result<BlogPost> getPostDetail(@PathVariable Long id) {
        try {
            return Result.success(blogService.getPostDetail(id));
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        }
    }

    /**
     * 创建文章
     * @param request 创建请求体
     * @param token 用户Token（Authorization请求头）
     * @return 创建的文章
     */
    @PostMapping("/posts")
    public Result<BlogPost> createPost(@RequestBody Map<String, Object> request,
                                        @RequestHeader("Authorization") String token) {
        try {
            return Result.success("发布成功", blogService.createPost(request, token));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新文章
     * @param id 文章ID
     * @param request 更新请求体
     * @param token 用户Token（Authorization请求头）
     * @return 更新后的文章
     */
    @PutMapping("/posts/{id}")
    public Result<BlogPost> updatePost(@PathVariable Long id,
                                        @RequestBody Map<String, Object> request,
                                        @RequestHeader("Authorization") String token) {
        try {
            return Result.success("更新成功", blogService.updatePost(id, request, token));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除文章
     * @param id 文章ID
     * @param token 用户Token（Authorization请求头）
     * @return 删除结果
     */
    @DeleteMapping("/posts/{id}")
    public Result<Void> deletePost(@PathVariable Long id,
                                    @RequestHeader("Authorization") String token) {
        try {
            blogService.deletePost(id, token);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取热门文章
     * @return 阅读量最高的前10篇文章
     */
    @GetMapping("/hot")
    public Result<List<BlogPost>> getHotPosts() {
        return Result.success(blogService.getHotPosts());
    }

    /**
     * 获取最新文章
     * @return 最新发布的前5篇文章
     */
    @GetMapping("/latest")
    public Result<List<BlogPost>> getLatestPosts() {
        return Result.success(blogService.getLatestPosts());
    }
}
