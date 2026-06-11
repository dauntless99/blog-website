package com.blog.service.repository;

import com.blog.service.entity.BlogLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 点赞数据访问层
 */
@Repository
public interface BlogLikeRepository extends JpaRepository<BlogLike, Long> {

    /**
     * 检查用户是否已点赞
     * @param postId 文章/评论ID
     * @param userId 用户ID
     * @param type 点赞类型
     * @return 是否已点赞
     */
    boolean existsByPostIdAndUserIdAndType(Long postId, Long userId, String type);

    /**
     * 根据文章ID查询点赞记录
     * @param postId 文章ID
     * @param type 点赞类型
     * @return 点赞记录列表
     */
    List<BlogLike> findByPostIdAndType(Long postId, String type);

    /**
     * 删除点赞记录
     * @param postId 文章/评论ID
     * @param userId 用户ID
     * @param type 点赞类型
     * @return 删除行数
     */
    @Modifying
    @Query("DELETE FROM BlogLike l WHERE l.postId = :postId AND l.userId = :userId AND l.type = :type")
    int deleteByPostIdAndUserIdAndType(@Param("postId") Long postId, @Param("userId") Long userId, @Param("type") String type);

    /**
     * 根据文章ID统计点赞数
     * @param postId 文章ID
     * @param type 点赞类型
     * @return 点赞数
     */
    long countByPostIdAndType(Long postId, String type);

    /**
     * 删除文章的所有点赞记录
     * @param postId 文章ID
     * @param type 点赞类型
     */
    void deleteByPostIdAndType(Long postId, String type);

    /**
     * 根据用户ID查询点赞记录
     * @param userId 用户ID
     * @param type 点赞类型
     * @return 点赞记录列表
     */
    List<BlogLike> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);
}