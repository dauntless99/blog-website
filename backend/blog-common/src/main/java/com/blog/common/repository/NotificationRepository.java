package com.blog.common.repository;

import com.blog.common.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 通知数据访问层
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 获取用户的通知列表（分页）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 通知分页
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 统计未读通知数量
     * @param userId 用户ID
     * @return 未读通知数
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * 标记通知为已读
     * @param userId 用户ID
     * @param notificationId 通知ID（可选，为null则标记所有）
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId " +
           "AND (:notificationId IS NULL OR n.id = :notificationId) AND n.isRead = false")
    void markAsRead(@Param("userId") Long userId, @Param("notificationId") Long notificationId);

    /**
     * 标记所有通知为已读
     * @param userId 用户ID
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") Long userId);

    /**
     * 删除用户的所有通知
     * @param userId 用户ID
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除特定通知
     * @param userId 用户ID
     * @param notificationId 通知ID
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId = :userId AND n.id = :notificationId")
    void deleteByIdAndUserId(@Param("userId") Long userId, @Param("notificationId") Long notificationId);
}