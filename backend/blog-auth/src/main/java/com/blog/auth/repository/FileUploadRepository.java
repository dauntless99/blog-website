package com.blog.auth.repository;

import com.blog.auth.entity.FileUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 文件上传记录数据访问层
 */
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    /**
     * 根据上传者ID查询文件
     * @param uploaderId 上传者ID
     * @param pageable 分页参数
     * @return 分页文件列表
     */
    Page<FileUpload> findByUploaderIdOrderByCreatedAtDesc(Long uploaderId, Pageable pageable);

    /**
     * 根据用途类型查询文件
     * @param usageType 用途类型
     * @param pageable 分页参数
     * @return 分页文件列表
     */
    Page<FileUpload> findByUsageTypeOrderByCreatedAtDesc(String usageType, Pageable pageable);

    /**
     * 根据关联ID查询文件
     * @param relatedId 关联ID
     * @return 文件列表
     */
    List<FileUpload> findByRelatedId(Long relatedId);

    /**
     * 根据存储文件名查询
     * @param storageName 存储文件名
     * @return 文件记录
     */
    FileUpload findByStorageName(String storageName);
}