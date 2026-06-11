package com.blog.auth.service;

import com.blog.auth.entity.Announcement;
import com.blog.auth.repository.AnnouncementRepository;
import com.blog.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告服务类
 * 提供公告管理相关业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    /**
     * 获取公告列表（已发布且未过期）
     * @param page 页码
     * @param size 每页大小
     * @return 分页公告列表
     */
    public Page<Announcement> getPublishedAnnouncements(int page, int size) {
        return announcementRepository.findByStatusAndExpireTimeAfterOrderByIsPinnedDescPublishTimeDesc(
                "published", LocalDateTime.now(), PageRequest.of(page, size));
    }

    /**
     * 获取所有公告（包含草稿、已归档）
     * @param page 页码
     * @param size 每页大小
     * @return 分页公告列表
     */
    public Page<Announcement> getAllAnnouncements(int page, int size) {
        return announcementRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * 获取置顶公告
     * @return 置顶公告列表
     */
    public List<Announcement> getPinnedAnnouncements() {
        return announcementRepository.findByStatusAndIsPinnedTrueAndExpireTimeAfter(
                "published", LocalDateTime.now());
    }

    /**
     * 根据ID获取公告详情
     * @param id 公告ID
     * @return 公告详情
     * @throws RuntimeException 公告不存在时抛出异常
     */
    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
    }

    /**
     * 创建公告（草稿状态）
     * @param request 创建请求
     * @param token 用户Token
     * @return 创建的公告
     */
    public Announcement createAnnouncement(java.util.Map<String, Object> request, String token) {
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        Announcement announcement = new Announcement();
        announcement.setTitle((String) request.get("title"));
        announcement.setContent((String) request.get("content"));
        announcement.setPublisherId(userId);
        announcement.setPublisherName(username);
        announcement.setStatus("draft");

        return announcementRepository.save(announcement);
    }

    /**
     * 发布公告
     * @param id 公告ID
     * @param token 用户Token
     * @return 发布后的公告
     * @throws RuntimeException 公告不存在时抛出异常
     */
    public Announcement publishAnnouncement(Long id, String token) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        announcement.setStatus("published");
        announcement.setPublishTime(LocalDateTime.now());
        
        // 设置过期时间（默认7天后）
        if (announcement.getExpireTime() == null) {
            announcement.setExpireTime(LocalDateTime.now().plusDays(7));
        }
        
        return announcementRepository.save(announcement);
    }

    /**
     * 更新公告
     * @param id 公告ID
     * @param request 更新请求
     * @return 更新后的公告
     * @throws RuntimeException 公告不存在时抛出异常
     */
    public Announcement updateAnnouncement(Long id, java.util.Map<String, Object> request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        if (request.containsKey("title")) {
            announcement.setTitle((String) request.get("title"));
        }
        if (request.containsKey("content")) {
            announcement.setContent((String) request.get("content"));
        }
        if (request.containsKey("isPinned")) {
            announcement.setIsPinned((Boolean) request.get("isPinned"));
        }
        if (request.containsKey("expireTime")) {
            announcement.setExpireTime(LocalDateTime.parse((String) request.get("expireTime")));
        }
        
        return announcementRepository.save(announcement);
    }

    /**
     * 归档公告
     * @param id 公告ID
     * @return 归档后的公告
     * @throws RuntimeException 公告不存在时抛出异常
     */
    public Announcement archiveAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        announcement.setStatus("archived");
        return announcementRepository.save(announcement);
    }

    /**
     * 删除公告
     * @param id 公告ID
     * @throws RuntimeException 公告不存在时抛出异常
     */
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new RuntimeException("公告不存在");
        }
        announcementRepository.deleteById(id);
    }
}