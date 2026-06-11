package com.blog.service.repository;

import com.blog.service.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章数据访问层
 */
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    Page<BlogPost> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Page<BlogPost> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    Page<BlogPost> findByCategoryAndStatusOrderByCreatedAtDesc(String category, String status, Pageable pageable);

    /**
     * 搜索文章（支持标题、内容、标签）
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页文章列表
     */
    @Query("SELECT b FROM BlogPost b WHERE b.status = 'published' AND (b.title LIKE %?1% OR b.content LIKE %?1% OR b.tags LIKE %?1%) ORDER BY b.createdAt DESC")
    Page<BlogPost> search(String keyword, Pageable pageable);

    List<BlogPost> findTop10ByStatusOrderByViewCountDesc(String status);

    List<BlogPost> findTop5ByStatusOrderByCreatedAtDesc(String status);

    /**
     * 更新阅读量
     * @param id 文章ID
     * @return 更新行数
     */
    @Modifying
    @Query("UPDATE BlogPost b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    int incrementViewCount(Long id);

    /**
     * 根据创建时间统计文章数量
     * @param dateTime 时间点
     * @return 文章数量
     */
    long countByCreatedAtAfter(LocalDateTime dateTime);

    /**
     * 根据状态统计文章数量
     * @param status 状态
     * @return 文章数量
     */
    long countByStatus(String status);
}
