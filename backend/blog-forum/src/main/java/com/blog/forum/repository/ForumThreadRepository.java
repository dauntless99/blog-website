package com.blog.forum.repository;

import com.blog.forum.entity.ForumThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * 论坛帖子数据访问层
 */
public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {

    Page<ForumThread> findByCategoryIdOrderByIsPinnedDescCreatedAtDesc(Long categoryId, Pageable pageable);

    Page<ForumThread> findByOrderByIsPinnedDescCreatedAtDesc(Pageable pageable);

    Page<ForumThread> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    /**
     * 搜索帖子（支持标题、内容搜索）
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页帖子列表
     */
    @Query("SELECT t FROM ForumThread t WHERE t.title LIKE %?1% OR t.content LIKE %?1% ORDER BY t.createdAt DESC")
    Page<ForumThread> search(String keyword, Pageable pageable);

    /**
     * 根据作者ID查询帖子
     * @param authorId 作者ID
     * @param pageable 分页参数
     * @return 分页帖子列表
     */
    Page<ForumThread> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    /**
     * 根据创建时间统计帖子数量
     * @param dateTime 时间点
     * @return 帖子数量
     */
    long countByCreatedAtAfter(java.time.LocalDateTime dateTime);
}
