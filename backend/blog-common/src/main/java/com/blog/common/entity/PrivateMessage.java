package com.blog.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 私信消息实体类
 * 对应数据库表: private_messages
 */
@Data
@Entity
@Table(name = "private_messages")
public class PrivateMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 发送者ID */
    @Column(nullable = false)
    private Long senderId;

    /** 发送者名称 */
    @Column(length = 30)
    private String senderName;

    /** 发送者头像 */
    @Column(length = 500)
    private String senderAvatar;

    /** 接收者ID */
    @Column(nullable = false)
    private Long receiverId;

    /** 接收者名称 */
    @Column(length = 30)
    private String receiverName;

    /** 消息内容 */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** 是否已读 */
    @Column(nullable = false)
    private Boolean isRead = false;

    /** 消息类型：text-文本, image-图片, file-文件 */
    @Column(length = 20)
    private String type = "text";

    /** 附件URL（如果是图片或文件） */
    @Column(length = 500)
    private String attachmentUrl;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 消息状态：sent-已发送, delivered-已送达, read-已读 */
    @Column(length = 20)
    private String status = "sent";
}