package com.blog.service.repository;

import com.blog.service.entity.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论数据访问层
 */
@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {

    /**
     * 根据文章ID查询评论（分页）
     * @param postId 文章ID
     * @param pageable 分页参数
     * @return 评论分页
     */
    Page<BlogComment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, String status, Pageable pageable);

    /**
     * 根据文章ID查询评论（不分页）
     * @param postId 文章ID
     * @return 评论列表
     */
    List<BlogComment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, String status);

    /**
     * 根据父评论ID查询子评论
     * @param parentId 父评论ID
     * @return 子评论列表
     */
    List<BlogComment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    /**
     * 根据文章ID统计评论数量
     * @param postId 文章ID
     * @param status 状态
     * @return 评论数量
     */
    long countByPostIdAndStatus(Long postId, String status);

    /**
     * 根据作者ID查询评论
     * @param authorId 作者ID
     * @param pageable 分页参数
     * @return 评论分页
     */
    Page<BlogComment> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    /**
     * 更新评论点赞数
     * @param commentId 评论ID
     * @param delta 变化量
     * @return 更新行数
     */
    @Modifying
    @Query("UPDATE BlogComment c SET c.likeCount = c.likeCount + :delta WHERE c.id = :commentId")
    int updateLikeCount(@Param("commentId") Long commentId, @Param("delta") int delta);

    /**
     * 批量更新文章下所有评论状态
     * @param postId 文章ID
     * @param status 新状态
     */
    @Modifying
    @Query("UPDATE BlogComment c SET c.status = :status WHERE c.postId = :postId")
    void updateStatusByPostId(@Param("postId") Long postId, @Param("status") String status);

    /**
     * 查询待审核的评论
     * @param pageable 分页参数
     * @return 待审核评论分页
     */
    Page<BlogComment> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    /**
     * 删除文章的所有评论
     * @param postId 文章ID
     */
    void deleteByPostId(Long postId);

    /**
     * 根据创建时间统计评论数量
     * @param dateTime 时间点
     * @return 评论数量
     */
    long countByCreatedAtAfter(LocalDateTime dateTime);
}