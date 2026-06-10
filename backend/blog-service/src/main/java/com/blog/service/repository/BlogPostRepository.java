package com.blog.service.repository;

import com.blog.service.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    Page<BlogPost> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Page<BlogPost> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    Page<BlogPost> findByCategoryAndStatusOrderByCreatedAtDesc(String category, String status, Pageable pageable);

    @Query("SELECT b FROM BlogPost b WHERE b.status = 'published' AND (b.title LIKE %?1% OR b.content LIKE %?1% OR b.tags LIKE %?1%) ORDER BY b.createdAt DESC")
    Page<BlogPost> search(String keyword, Pageable pageable);

    List<BlogPost> findTop10ByStatusOrderByViewCountDesc(String status);

    List<BlogPost> findTop5ByStatusOrderByCreatedAtDesc(String status);
}
