package com.blog.common.service;

import com.blog.auth.entity.User;
import com.blog.auth.repository.UserRepository;
import com.blog.common.entity.PrivateMessage;
import com.blog.common.repository.PrivateMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 私信服务类
 * 提供私信的发送、接收、查询等功能
 */
@Service
@RequiredArgsConstructor
public class PrivateMessageService {

    private final PrivateMessageRepository messageRepository;
    private final UserRepository userRepository;

    /**
     * 发送私信
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @return 发送的消息
     */
    @Transactional
    public PrivateMessage sendMessage(Long senderId, Long receiverId, String content) {
        // 获取发送者信息
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("发送者不存在"));
        
        // 获取接收者信息
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("接收者不存在"));

        // 创建消息
        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setSenderName(sender.getNickname() != null ? sender.getNickname() : sender.getUsername());
        message.setSenderAvatar(sender.getAvatar());
        message.setReceiverId(receiverId);
        message.setReceiverName(receiver.getNickname() != null ? receiver.getNickname() : receiver.getUsername());
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        message.setType("text");

        return messageRepository.save(message);
    }

    /**
     * 获取用户的对话列表
     * @param userId 用户ID
     * @return 对话列表（每个对方一条最新消息）
     */
    public List<PrivateMessage> getConversations(Long userId) {
        return messageRepository.findConversations(userId);
    }

    /**
     * 获取与某个用户的消息记录
     * @param userId 当前用户ID
     * @param otherUserId 对方用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 消息分页
     */
    public Page<PrivateMessage> getMessages(Long userId, Long otherUserId, int page, int size) {
        return messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtDesc(
                userId, otherUserId, otherUserId, userId, PageRequest.of(page, size));
    }

    /**
     * 获取未读消息数量
     * @param userId 用户ID
     * @return 未读消息数
     */
    public long getUnreadCount(Long userId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    /**
     * 获取与某个用户的未读消息数量
     * @param userId 当前用户ID
     * @param otherUserId 对方用户ID
     * @return 未读消息数
     */
    public long getUnreadCount(Long userId, Long otherUserId) {
        return messageRepository.countByReceiverIdAndSenderIdAndIsReadFalse(userId, otherUserId);
    }

    /**
     * 标记与某个用户的消息为已读
     * @param userId 当前用户ID
     * @param otherUserId 对方用户ID（可选，为null则标记所有）
     */
    @Transactional
    public void markAsRead(Long userId, Long otherUserId) {
        messageRepository.markAsRead(userId, otherUserId);
    }

    /**
     * 删除对话
     * @param userId 当前用户ID
     * @param otherUserId 对方用户ID
     */
    @Transactional
    public void deleteConversation(Long userId, Long otherUserId) {
        messageRepository.deleteConversation(userId, otherUserId);
    }

    /**
     * 删除单条消息
     * @param messageId 消息ID
     * @param userId 用户ID（验证权限）
     */
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        PrivateMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        // 只能删除自己发送或接收的消息
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权删除此消息");
        }
        
        messageRepository.delete(message);
    }
}