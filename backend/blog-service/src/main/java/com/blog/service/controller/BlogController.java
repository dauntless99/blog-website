package com.blog.service.controller;

import com.blog.common.result.Result;
import com.blog.service.entity.BlogPost;
import com.blog.service.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

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

    @GetMapping("/posts/{id}")
    public Result<BlogPost> getPostDetail(@PathVariable Long id) {
        try {
            return Result.success(blogService.getPostDetail(id));
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        }
    }

    @PostMapping("/posts")
    public Result<BlogPost> createPost(@RequestBody Map<String, Object> request,
                                        @RequestHeader("Authorization") String token) {
        try {
            return Result.success("发布成功", blogService.createPost(request, token));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

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

    @GetMapping("/hot")
    public Result<List<BlogPost>> getHotPosts() {
        return Result.success(blogService.getHotPosts());
    }

    @GetMapping("/latest")
    public Result<List<BlogPost>> getLatestPosts() {
        return Result.success(blogService.getLatestPosts());
    }
}
