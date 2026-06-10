package com.blog.forum.service;

import com.blog.common.util.JwtUtil;
import com.blog.forum.entity.ForumCategory;
import com.blog.forum.entity.ForumReply;
import com.blog.forum.entity.ForumThread;
import com.blog.forum.repository.ForumCategoryRepository;
import com.blog.forum.repository.ForumReplyRepository;
import com.blog.forum.repository.ForumThreadRepository;
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
public class ForumService {

    private final ForumCategoryRepository categoryRepository;
    private final ForumThreadRepository threadRepository;
    private final ForumReplyRepository replyRepository;

    // ===== 分类 =====
    public List<ForumCategory> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    public ForumCategory createCategory(ForumCategory category) {
        return categoryRepository.save(category);
    }

    // ===== 帖子 =====
    public Page<ForumThread> getThreads(Long categoryId, int page, int size, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size);
        if (keyword != null && !keyword.isEmpty()) {
            return threadRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageRequest);
        }
        if (categoryId != null && categoryId > 0) {
            return threadRepository.findByCategoryIdOrderByIsPinnedDescCreatedAtDesc(categoryId, pageRequest);
        }
        return threadRepository.findByOrderByIsPinnedDescCreatedAtDesc(pageRequest);
    }

    public ForumThread getThreadDetail(Long id) {
        ForumThread thread = threadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        thread.setViewCount(thread.getViewCount() + 1);
        threadRepository.save(thread);
        return thread;
    }

    public ForumThread createThread(Map<String, Object> request, String token) {
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        ForumThread thread = new ForumThread();
        thread.setTitle((String) request.get("title"));
        thread.setContent((String) request.get("content"));
        thread.setAuthorId(userId);
        thread.setAuthorName(username);
        thread.setCategoryId(Long.valueOf(request.get("categoryId").toString()));

        ForumCategory category = categoryRepository.findById(thread.getCategoryId())
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        thread.setCategoryName(category.getName());

        thread = threadRepository.save(thread);

        category.setThreadCount(category.getThreadCount() + 1);
        categoryRepository.save(category);

        return thread;
    }

    // ===== 回复 =====
    public Page<ForumReply> getReplies(Long threadId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        return replyRepository.findByThreadIdOrderByCreatedAtAsc(threadId, pageRequest);
    }

    public ForumReply createReply(Map<String, Object> request, String token) {
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        ForumReply reply = new ForumReply();
        reply.setContent((String) request.get("content"));
        reply.setThreadId(Long.valueOf(request.get("threadId").toString()));
        reply.setAuthorId(userId);
        reply.setAuthorName(username);
        if (request.containsKey("parentId") && request.get("parentId") != null) {
            reply.setParentId(Long.valueOf(request.get("parentId").toString()));
        }

        reply = replyRepository.save(reply);

        // 更新帖子回复数
        ForumThread thread = threadRepository.findById(reply.getThreadId())
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        thread.setReplyCount((int) replyRepository.countByThreadId(reply.getThreadId()));
        threadRepository.save(thread);

        return reply;
    }

    @Transactional
    public void deleteThread(Long id, String token) {
        ForumThread thread = threadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        Long userId = JwtUtil.getUserId(token);
        if (!thread.getAuthorId().equals(userId)) {
            throw new RuntimeException("无权删除此帖子");
        }
        replyRepository.findByThreadIdOrderByCreatedAtAsc(id).forEach(reply ->
                replyRepository.delete(reply)
        );
        threadRepository.delete(thread);
    }
}
