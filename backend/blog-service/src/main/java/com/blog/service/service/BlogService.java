package com.blog.service.service;

import com.blog.common.util.JwtUtil;
import com.blog.service.entity.BlogPost;
import com.blog.service.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogPostRepository blogPostRepository;

    public Page<BlogPost> getPostList(int page, int size, String category, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (keyword != null && !keyword.isEmpty()) {
            return blogPostRepository.search(keyword, pageRequest);
        }
        if (category != null && !category.isEmpty()) {
            return blogPostRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, "published", pageRequest);
        }
        return blogPostRepository.findByStatusOrderByCreatedAtDesc("published", pageRequest);
    }

    public BlogPost getPostDetail(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        post.setViewCount(post.getViewCount() + 1);
        blogPostRepository.save(post);
        return post;
    }

    public BlogPost createPost(Map<String, Object> request, String token) {
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        BlogPost post = new BlogPost();
        post.setTitle((String) request.get("title"));
        post.setContent((String) request.get("content"));
        post.setSummary((String) request.getOrDefault("summary", ""));
        post.setAuthorId(userId);
        post.setAuthorName(username);
        post.setCategory((String) request.getOrDefault("category", "默认"));
        post.setTags((String) request.getOrDefault("tags", ""));
        post.setStatus((String) request.getOrDefault("status", "published"));

        if (post.getSummary().isEmpty() && post.getContent().length() > 200) {
            post.setSummary(post.getContent().substring(0, 200) + "...");
        } else if (post.getSummary().isEmpty()) {
            post.setSummary(post.getContent());
        }

        return blogPostRepository.save(post);
    }

    public BlogPost updatePost(Long id, Map<String, Object> request, String token) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        Long userId = JwtUtil.getUserId(token);
        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("无权修改此文章");
        }

        if (request.containsKey("title")) post.setTitle((String) request.get("title"));
        if (request.containsKey("content")) post.setContent((String) request.get("content"));
        if (request.containsKey("summary")) post.setSummary((String) request.get("summary"));
        if (request.containsKey("category")) post.setCategory((String) request.get("category"));
        if (request.containsKey("tags")) post.setTags((String) request.get("tags"));
        if (request.containsKey("status")) post.setStatus((String) request.get("status"));

        return blogPostRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id, String token) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        Long userId = JwtUtil.getUserId(token);
        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("无权删除此文章");
        }

        blogPostRepository.delete(post);
    }

    public List<BlogPost> getHotPosts() {
        return blogPostRepository.findTop10ByStatusOrderByViewCountDesc("published");
    }

    public List<BlogPost> getLatestPosts() {
        return blogPostRepository.findTop5ByStatusOrderByCreatedAtDesc("published");
    }
}
