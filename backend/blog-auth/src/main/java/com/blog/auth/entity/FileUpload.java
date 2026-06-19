package com.blog.auth.entity;

import com.blog.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件上传记录实体类
 * 对应数据库表: file_uploads
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "file_uploads")
public class FileUpload extends BaseEntity {
    /** 原始文件名 */
    @Column(nullable = false, length = 255)
    private String originalName;

    /** 存储文件名（UUID） */
    @Column(nullable = false, length = 255)
    private String storageName;

    /** 文件路径 */
    @Column(nullable = false, length = 500)
    private String filePath;

    /** 文件大小（字节） */
    @Column(nullable = false)
    private Long fileSize;

    /** 文件类型（MIME类型） */
    @Column(length = 100)
    private String contentType;

    /** 上传者ID */
    @Column(nullable = false)
    private Long uploaderId;

    /** 上传者名称 */
    @Column(length = 30)
    private String uploaderName;

    /** 文件用途：avatar-头像，post-文章附件，forum-论坛附件，other-其他 */
    @Column(length = 20)
    private String usageType = "other";

    /** 关联ID（如文章ID、帖子ID） */
    @Column
    private Long relatedId;

    /** 是否删除 */
    @Column(nullable = false)
    private Boolean deleted = false;
}
