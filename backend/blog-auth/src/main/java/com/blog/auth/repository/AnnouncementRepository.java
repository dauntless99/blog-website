package com.blog.auth.repository;

import com.blog.auth.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告数据访问层
 */
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /**
     * 查询已发布且未过期的公告（按置顶和发布时间排序）
     * @param status 状态
     * @param now 当前时间
     * @param pageable 分页参数
     * @return 分页公告列表
     */
    Page<Announcement> findByStatusAndExpireTimeAfterOrderByIsPinnedDescPublishTimeDesc(
            String status, LocalDateTime now, Pageable pageable);

    /**
     * 查询已发布的公告（不考虑过期时间）
     * @param status 状态
     * @param pageable 分页参数
     * @return 分页公告列表
     */
    Page<Announcement> findByStatusOrderByIsPinnedDescPublishTimeDesc(String status, Pageable pageable);

    /**
     * 查询置顶的已发布公告
     * @param status 状态
     * @param isPinned 是否置顶
     * @param now 当前时间
     * @return 置顶公告列表
     */
    List<Announcement> findByStatusAndIsPinnedTrueAndExpireTimeAfter(String status, LocalDateTime now);

    /**
     * 根据发布者ID查询公告
     * @param publisherId 发布者ID
     * @param pageable 分页参数
     * @return 分页公告列表
     */
    Page<Announcement> findByPublisherIdOrderByCreatedAtDesc(Long publisherId, Pageable pageable);

    /**
     * 查询所有公告并按创建时间倒序排序
     * @param pageable 分页参数
     * @return 分页公告列表
     */
    Page<Announcement> findAllByOrderByCreatedAtDesc(Pageable pageable);
}