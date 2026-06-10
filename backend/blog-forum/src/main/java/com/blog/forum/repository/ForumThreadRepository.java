package com.blog.forum.repository;

import com.blog.forum.entity.ForumThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {

    Page<ForumThread> findByCategoryIdOrderByIsPinnedDescCreatedAtDesc(Long categoryId, Pageable pageable);

    Page<ForumThread> findByOrderByIsPinnedDescCreatedAtDesc(Pageable pageable);

    Page<ForumThread> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
