package com.blog.service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.blog.service.entity.BlogPost;
import com.blog.service.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

/**
 * 搜索服务类
 * 提供全文搜索、高级筛选、热门标签等功能
 */
@Service
@RequiredArgsConstructor
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final BlogPostRepository blogPostRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${search.forum-service-base-url:http://localhost:8083}")
    private String forumServiceBaseUrl;

    /**
     * 统一搜索接口（同时搜索文章和帖子）
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 搜索结果，包含文章和帖子
     */
    public Map<String, Object> unifiedSearch(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<BlogPost> blogResults = blogPostRepository.search(keyword, pageable);
        ForumSearchPage forumResults = fetchForumThreads(keyword, null, null, null, null, page, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("blogPosts", blogResults.getContent());
        result.put("forumThreads", forumResults.content());
        result.put("totalBlogCount", blogResults.getTotalElements());
        result.put("totalForumCount", forumResults.totalElements());
        
        return result;
    }

    /**
     * 高级搜索文章
     * @param keyword 关键词（可选）
     * @param category 分类（可选）
     * @param authorId 作者ID（可选）
     * @param startTime 开始时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime 结束时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @param sortBy 排序字段：createdAt（默认）、viewCount、likeCount
     * @param sortOrder 排序方向：asc、desc（默认）
     * @param page 页码
     * @param size 每页大小
     * @return 分页文章列表
     */
    public Page<BlogPost> advancedSearchPosts(
            String keyword, String category, Long authorId,
            String startTime, String endTime,
            String sortBy, String sortOrder,
            int page, int size) {
        
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "createdAt"
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 根据条件构建查询
        if (keyword != null && !keyword.isEmpty()) {
            return blogPostRepository.search(keyword, pageable);
        }
        if (category != null && !category.isEmpty()) {
            return blogPostRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, "published", pageable);
        }
        if (authorId != null) {
            return blogPostRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
        }
        
        return blogPostRepository.findByStatusOrderByCreatedAtDesc("published", pageable);
    }

    /**
     * 高级搜索帖子
     * @param keyword 关键词（可选）
     * @param categoryId 分类ID（可选）
     * @param authorId 作者ID（可选）
     * @param sortBy 排序字段：createdAt（默认）、viewCount、replyCount
     * @param sortOrder 排序方向：asc、desc（默认）
     * @param page 页码
     * @param size 每页大小
     * @return 分页帖子列表
     */
    public Page<Map<String, Object>> advancedSearchThreads(
            String keyword, Long categoryId, Long authorId,
            String sortBy, String sortOrder,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        ForumSearchPage forumResults = fetchForumThreads(keyword, categoryId, authorId, sortBy, sortOrder, page, size);
        return new PageImpl<>(forumResults.content(), pageable, forumResults.totalElements());
    }

    /**
     * 获取热门标签（基于文章标签统计）
     * @param limit 返回数量限制
     * @return 热门标签列表，按使用次数降序排列
     */
    public List<Map<String, Object>> getHotTags(int limit) {
        List<BlogPost> posts = blogPostRepository.findAll();
        Map<String, Integer> tagCount = new HashMap<>();
        
        for (BlogPost post : posts) {
            String tags = post.getTags();
            if (tags != null && !tags.isEmpty()) {
                String[] tagArray = tags.split(",");
                for (String tag : tagArray) {
                    tag = tag.trim();
                    if (!tag.isEmpty()) {
                        tagCount.merge(tag, 1, Integer::sum);
                    }
                }
            }
        }
        
        // 排序并返回前N个热门标签
        return tagCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .toList();
    }

    /**
     * 获取分类统计
     * @return 各分类的文章数量统计
     */
    public List<Map<String, Object>> getCategoryStats() {
        List<BlogPost> posts = blogPostRepository.findAll();
        Map<String, Long> categoryCount = posts.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        BlogPost::getCategory,
                        java.util.stream.Collectors.counting()
                ));
        
        return categoryCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .toList();
    }

    /**
     * 搜索建议（根据关键词前缀匹配）
     * @param prefix 关键词前缀
     * @param limit 返回数量限制
     * @return 建议的关键词列表
     */
    public List<String> getSearchSuggestions(String prefix, int limit) {
        if (prefix == null || prefix.isEmpty()) {
            return Collections.emptyList();
        }
        
        Set<String> suggestions = new HashSet<>();
        
        // 从文章标题和标签中获取建议
        List<BlogPost> posts = blogPostRepository.search(prefix, PageRequest.of(0, 50)).getContent();
        for (BlogPost post : posts) {
            if (post.getTitle().toLowerCase().contains(prefix.toLowerCase())) {
                suggestions.add(post.getTitle());
            }
            if (post.getTags() != null) {
                String[] tags = post.getTags().split(",");
                for (String tag : tags) {
                    tag = tag.trim().toLowerCase();
                    if (tag.startsWith(prefix.toLowerCase())) {
                        suggestions.add(tag);
                    }
                }
            }
        }
        
        return suggestions.stream()
                .sorted()
                .limit(limit)
                .toList();
    }

    private ForumSearchPage fetchForumThreads(String keyword, Long categoryId, Long authorId,
                                              String sortBy, String sortOrder, int page, int size) {
        try {
            String responseBody = RestClient.create()
                    .get()
                    .uri(forumServiceBaseUrl + "/api/forum/threads?keyword={keyword}&categoryId={categoryId}&authorId={authorId}&sortBy={sortBy}&sortOrder={sortOrder}&page={page}&size={size}",
                            keyword, categoryId, authorId, sortBy, sortOrder, page, size)
                    .retrieve()
                    .body(String.class);

            Map<String, Object> response = objectMapper.readValue(responseBody, new TypeReference<>() {});
            Object dataObject = response.get("data");
            if (!(dataObject instanceof Map<?, ?> data)) {
                return ForumSearchPage.empty();
            }

            List<Map<String, Object>> content = extractContent(data.get("content"));
            Object totalElementsValue = data.containsKey("totalElements") ? data.get("totalElements") : 0L;
            long totalElements = totalElementsValue instanceof Number number ? number.longValue() : 0L;
            return new ForumSearchPage(content, totalElements);
        } catch (Exception e) {
            log.warn("论坛搜索调用失败，已降级为空结果: {}", e.getMessage());
            return ForumSearchPage.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractContent(Object contentObject) {
        if (!(contentObject instanceof List<?> contentList)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object item : contentList) {
            if (item instanceof Map<?, ?> itemMap) {
                result.add((Map<String, Object>) itemMap);
            }
        }
        return result;
    }

    private record ForumSearchPage(List<Map<String, Object>> content, long totalElements) {
        private static ForumSearchPage empty() {
            return new ForumSearchPage(Collections.emptyList(), 0);
        }
    }
}
