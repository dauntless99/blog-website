package com.blog.common.repository;

import com.blog.common.entity.PrivateMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私信数据访问层
 */
@Repository
public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {

    /**
     * 获取用户的私信对话列表（按对方分组）
     * @param userId 用户ID
     * @return 对话列表（每个对方一条最新消息）
     */
    @Query("SELECT m FROM PrivateMessage m WHERE m.id IN " +
           "(SELECT MAX(m2.id) FROM PrivateMessage m2 WHERE m2.senderId = :userId OR m2.receiverId = :userId " +
           "GROUP BY CASE WHEN m2.senderId = :userId THEN m2.receiverId ELSE m2.senderId END)")
    List<PrivateMessage> findConversations(@Param("userId") Long userId);

    /**
     * 获取两个用户之间的消息（分页）
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @param pageable 分页参数
     * @return 消息分页
     */
    Page<PrivateMessage> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtDesc(
            Long userId1, Long userId2, Long userId2Again, Long userId1Again, Pageable pageable);

    /**
     * 统计未读消息数量
     * @param userId 用户ID
     * @return 未读消息数
     */
    long countByReceiverIdAndIsReadFalse(Long userId);

    /**
     * 统计与某个用户的未读消息数量
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @return 未读消息数
     */
    long countByReceiverIdAndSenderIdAndIsReadFalse(Long receiverId, Long senderId);

    /**
     * 标记消息为已读
     * @param receiverId 接收者ID
     * @param senderId 发送者ID（可选，为null则标记所有）
     */
    @Modifying
    @Query("UPDATE PrivateMessage m SET m.isRead = true, m.status = 'read' WHERE m.receiverId = :receiverId " +
           "AND (:senderId IS NULL OR m.senderId = :senderId) AND m.isRead = false")
    void markAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

    /**
     * 删除用户的所有消息
     * @param userId 用户ID
     */
    @Modifying
    @Query("DELETE FROM PrivateMessage m WHERE m.senderId = :userId OR m.receiverId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除两个用户之间的所有消息
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     */
    @Modifying
    @Query("DELETE FROM PrivateMessage m WHERE " +
           "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1)")
    void deleteConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}