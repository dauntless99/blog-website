package com.blog.auth.controller;

import com.blog.auth.entity.FileUpload;
import com.blog.auth.service.FileService;
import com.blog.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 文件控制器
 * 处理文件上传、下载相关请求
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 上传文件
     * @param file 上传的文件
     * @param token 用户Token（Authorization请求头）
     * @param usageType 文件用途（可选）
     * @param relatedId 关联ID（可选）
     * @return 文件上传记录
     */
    @PostMapping("/upload")
    public Result<FileUpload> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String usageType,
            @RequestParam(required = false) Long relatedId) {
        try {
            return Result.success("上传成功", fileService.uploadFile(file, token, usageType, relatedId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 下载文件
     * @param id 文件ID
     * @return 文件资源
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            FileUpload fileUpload = fileService.getFileById(id);
            String filePath = fileService.getFilePath(fileUpload);
            
            Resource resource = new FileSystemResource(filePath);
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String encodedFileName = URLEncoder.encode(fileUpload.getOriginalName(), StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileUpload.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + encodedFileName + "\"")
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取文件信息
     * @param id 文件ID
     * @return 文件信息
     */
    @GetMapping("/{id}")
    public Result<FileUpload> getFileInfo(@PathVariable Long id) {
        try {
            return Result.success(fileService.getFileById(id));
        } catch (RuntimeException e) {
            return Result.notFound(e.getMessage());
        }
    }

    /**
     * 获取用户上传的文件列表
     * @param userId 用户ID
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @return 分页文件列表
     */
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getUserFiles(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<FileUpload> filePage = fileService.getFilesByUser(userId, page, size);
        return Result.success(Map.of(
                "content", filePage.getContent(),
                "totalElements", filePage.getTotalElements(),
                "totalPages", filePage.getTotalPages(),
                "currentPage", filePage.getNumber(),
                "size", filePage.getSize()
        ));
    }

    /**
     * 删除文件（逻辑删除）
     * @param id 文件ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 物理删除文件
     * @param id 文件ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}/physical")
    public Result<Void> physicalDeleteFile(@PathVariable Long id) {
        try {
            fileService.physicalDeleteFile(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}