package com.blog.service.controller;

import com.blog.common.result.Result;
import com.blog.service.entity.BlogPost;
import com.blog.service.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 * 提供全文搜索、高级筛选、热门标签等接口
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 统一搜索（同时搜索文章和帖子）
     * @param keyword 搜索关键词
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @return 搜索结果
     */
    @GetMapping("/unified")
    public Result<Map<String, Object>> unifiedSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(searchService.unifiedSearch(keyword, page, size));
    }

    /**
     * 高级搜索文章
     * @param keyword 关键词（可选）
     * @param category 分类（可选）
     * @param authorId 作者ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param sortBy 排序字段（可选，默认createdAt）
     * @param sortOrder 排序方向（可选，默认desc）
     * @param page 页码（默认0）
     * @param size 每页大小（默认10）
     * @return 分页文章列表
     */
    @GetMapping("/posts")
    public Result<Map<String, Object>> advancedSearchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<BlogPost> postPage = searchService.advancedSearchPosts(
                keyword, category, authorId, startTime, endTime, sortBy, sortOrder, page, size);
        
        return Result.success(Map.of(
                "content", postPage.getContent(),
                "totalElements", postPage.getTotalElements(),
                "totalPages", postPage.getTotalPages(),
                "currentPage", postPage.getNumber(),
                "size", postPage.getSize()
        ));
    }

    /**
     * 高级搜索帖子
     * @param keyword 关键词（可选）
     * @param categoryId 分类ID（可选）
     * @param authorId 作者ID（可选）
     * @param sortBy 排序字段（可选，默认createdAt）
     * @param sortOrder 排序方向（可选，默认desc）
     * @param page 页码（默认0）
     * @param size 每页大小（默认10）
     * @return 分页帖子列表
     */
    @GetMapping("/threads")
    public Result<Map<String, Object>> advancedSearchThreads(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Map<String, Object>> threadPage = searchService.advancedSearchThreads(
                keyword, categoryId, authorId, sortBy, sortOrder, page, size);
        
        return Result.success(Map.of(
                "content", threadPage.getContent(),
                "totalElements", threadPage.getTotalElements(),
                "totalPages", threadPage.getTotalPages(),
                "currentPage", threadPage.getNumber(),
                "size", threadPage.getSize()
        ));
    }

    /**
     * 获取热门标签
     * @param limit 返回数量限制（默认20）
     * @return 热门标签列表
     */
    @GetMapping("/hot-tags")
    public Result<List<Map<String, Object>>> getHotTags(
            @RequestParam(defaultValue = "20") int limit) {
        return Result.success(searchService.getHotTags(limit));
    }

    /**
     * 获取分类统计
     * @return 各分类文章数量统计
     */
    @GetMapping("/category-stats")
    public Result<List<Map<String, Object>>> getCategoryStats() {
        return Result.success(searchService.getCategoryStats());
    }

    /**
     * 获取搜索建议
     * @param prefix 关键词前缀
     * @param limit 返回数量限制（默认10）
     * @return 建议关键词列表
     */
    @GetMapping("/suggestions")
    public Result<List<String>> getSearchSuggestions(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(searchService.getSearchSuggestions(prefix, limit));
    }
}
