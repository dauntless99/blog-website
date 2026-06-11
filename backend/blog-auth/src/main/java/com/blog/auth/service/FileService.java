package com.blog.auth.service;

import com.blog.auth.entity.FileUpload;
import com.blog.auth.repository.FileUploadRepository;
import com.blog.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件服务类
 * 提供文件上传、下载、管理相关业务逻辑
 */
@Service
@RequiredArgsConstructor
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    private final FileUploadRepository fileUploadRepository;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * 上传文件
     * @param file 上传的文件
     * @param token 用户Token
     * @param usageType 文件用途
     * @param relatedId 关联ID（可选）
     * @return 文件上传记录
     * @throws RuntimeException 文件上传失败时抛出异常
     */
    public FileUpload uploadFile(MultipartFile file, String token, String usageType, Long relatedId) {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择要上传的文件");
        }

        // 验证文件大小（最大10MB）
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("文件大小不能超过10MB");
        }

        // 获取用户信息
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        // 生成存储文件名
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String storageName = UUID.randomUUID().toString() + extension;

        // 构建文件路径
        String datePath = java.time.LocalDate.now().toString().replace("-", "/");
        String relativePath = datePath + "/" + storageName;
        Path filePath = Paths.get(uploadDir, datePath, storageName);

        try {
            // 确保目录存在
            Files.createDirectories(filePath.getParent());
            // 保存文件
            file.transferTo(filePath.toFile());

            // 记录上传信息
            FileUpload upload = new FileUpload();
            upload.setOriginalName(originalName);
            upload.setStorageName(storageName);
            upload.setFilePath(relativePath);
            upload.setFileSize(file.getSize());
            upload.setContentType(file.getContentType());
            upload.setUploaderId(userId);
            upload.setUploaderName(username);
            upload.setUsageType(usageType != null ? usageType : "other");
            upload.setRelatedId(relatedId);

            return fileUploadRepository.save(upload);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取文件信息
     * @param id 文件ID
     * @return 文件上传记录
     * @throws RuntimeException 文件不存在时抛出异常
     */
    public FileUpload getFileById(Long id) {
        return fileUploadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
    }

    /**
     * 根据存储文件名获取文件信息
     * @param storageName 存储文件名
     * @return 文件上传记录
     * @throws RuntimeException 文件不存在时抛出异常
     */
    public FileUpload getFileByStorageName(String storageName) {
        FileUpload file = fileUploadRepository.findByStorageName(storageName);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }
        return file;
    }

    /**
     * 获取文件的绝对路径
     * @param fileUpload 文件上传记录
     * @return 文件绝对路径
     */
    public String getFilePath(FileUpload fileUpload) {
        return Paths.get(uploadDir, fileUpload.getFilePath()).toString();
    }

    /**
     * 获取用户上传的文件列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页文件列表
     */
    public Page<FileUpload> getFilesByUser(Long userId, int page, int size) {
        return fileUploadRepository.findByUploaderIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    /**
     * 删除文件（逻辑删除）
     * @param id 文件ID
     * @throws RuntimeException 文件不存在时抛出异常
     */
    public void deleteFile(Long id) {
        FileUpload file = fileUploadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        file.setDeleted(true);
        fileUploadRepository.save(file);
    }

    /**
     * 物理删除文件
     * @param id 文件ID
     * @throws RuntimeException 文件不存在或删除失败时抛出异常
     */
    public void physicalDeleteFile(Long id) {
        FileUpload file = fileUploadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文件不存在"));

        try {
            Path filePath = Paths.get(uploadDir, file.getFilePath());
            Files.deleteIfExists(filePath);
            fileUploadRepository.delete(file);
        } catch (IOException e) {
            log.error("文件删除失败: {}", e.getMessage());
            throw new RuntimeException("文件删除失败：" + e.getMessage());
        }
    }
}