package com.blog.auth.service;

import com.blog.auth.entity.PointRecord;
import com.blog.auth.repository.PointRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 积分服务类
 * 提供积分管理、积分记录等功能
 */
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRecordRepository pointRecordRepository;

    /**
     * 积分变动类型常量
     */
    public static final String POINT_TYPE_REGISTER = "REGISTER";
    public static final String POINT_TYPE_LOGIN = "LOGIN";
    public static final String POINT_TYPE_POST_CREATE = "POST_CREATE";
    public static final String POINT_TYPE_COMMENT = "COMMENT";
    public static final String POINT_TYPE_LIKE = "LIKE";
    public static final String POINT_TYPE_POST_VIEW = "POST_VIEW";
    public static final String POINT_TYPE_SYSTEM = "SYSTEM";

    /**
     * 积分奖励配置
     */
    public static final int POINTS_FOR_REGISTER = 100;
    public static final int POINTS_FOR_DAILY_LOGIN = 10;
    public static final int POINTS_FOR_POST = 20;
    public static final int POINTS_FOR_COMMENT = 5;
    public static final int POINTS_FOR_LIKE_RECEIVED = 2;
    public static final int POINTS_FOR_VIEW = 1;

    /**
     * 获取用户当前积分
     * @param userId 用户ID
     * @return 当前积分
     */
    public int getUserPoints(Long userId) {
        return pointRecordRepository.sumPointsByUserId(userId);
    }

    /**
     * 添加积分
     * @param userId 用户ID
     * @param points 积分数（正数）
     * @param type 变动类型
     * @param targetId 相关业务ID
     * @param description 变动描述
     * @return 更新后的积分
     */
    @Transactional
    public int addPoints(Long userId, int points, String type, Long targetId, String description) {
        if (points <= 0) {
            throw new IllegalArgumentException("积分必须为正数");
        }
        return changePoints(userId, points, type, targetId, description);
    }

    /**
     * 扣除积分
     * @param userId 用户ID
     * @param points 积分数（正数）
     * @param type 变动类型
     * @param targetId 相关业务ID
     * @param description 变动描述
     * @return 更新后的积分
     */
    @Transactional
    public int deductPoints(Long userId, int points, String type, Long targetId, String description) {
        int currentPoints = getUserPoints(userId);
        if (currentPoints < points) {
            throw new RuntimeException("积分不足");
        }
        return changePoints(userId, -points, type, targetId, description);
    }

    /**
     * 修改积分（内部方法）
     * @param userId 用户ID
     * @param points 积分数（可正可负）
     * @param type 变动类型
     * @param targetId 相关业务ID
     * @param description 变动描述
     * @return 更新后的积分
     */
    private int changePoints(Long userId, int points, String type, Long targetId, String description) {
        int beforePoints = getUserPoints(userId);
        int afterPoints = beforePoints + points;

        PointRecord record = new PointRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType(type);
        record.setTargetId(targetId);
        record.setDescription(description);
        record.setBeforePoints(beforePoints);
        record.setAfterPoints(afterPoints);

        pointRecordRepository.save(record);

        return afterPoints;
    }

    /**
     * 用户注册奖励积分
     * @param userId 用户ID
     * @return 更新后的积分
     */
    @Transactional
    public int rewardRegister(Long userId) {
        return addPoints(userId, POINTS_FOR_REGISTER, POINT_TYPE_REGISTER, null, "注册奖励");
    }

    /**
     * 每日登录奖励积分
     * @param userId 用户ID
     * @return 更新后的积分
     */
    @Transactional
    public int rewardDailyLogin(Long userId) {
        return addPoints(userId, POINTS_FOR_DAILY_LOGIN, POINT_TYPE_LOGIN, null, "每日登录奖励");
    }

    /**
     * 发布文章奖励积分
     * @param userId 用户ID
     * @param postId 文章ID
     * @return 更新后的积分
     */
    @Transactional
    public int rewardPostCreate(Long userId, Long postId) {
        return addPoints(userId, POINTS_FOR_POST, POINT_TYPE_POST_CREATE, postId, "发布文章奖励");
    }

    /**
     * 发表评论奖励积分
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 更新后的积分
     */
    @Transactional
    public int rewardComment(Long userId, Long commentId) {
        return addPoints(userId, POINTS_FOR_COMMENT, POINT_TYPE_COMMENT, commentId, "发表评论奖励");
    }

    /**
     * 收到点赞奖励积分
     * @param userId 用户ID
     * @param targetId 被点赞内容ID
     * @return 更新后的积分
     */
    @Transactional
    public int rewardLikeReceived(Long userId, Long targetId) {
        return addPoints(userId, POINTS_FOR_LIKE_RECEIVED, POINT_TYPE_LIKE, targetId, "收到点赞奖励");
    }

    /**
     * 文章被浏览奖励积分
     * @param userId 用户ID
     * @param postId 文章ID
     * @return 更新后的积分
     */
    @Transactional
    public int rewardPostView(Long userId, Long postId) {
        return addPoints(userId, POINTS_FOR_VIEW, POINT_TYPE_POST_VIEW, postId, "文章被浏览");
    }

    /**
     * 获取用户积分记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 积分记录分页
     */
    public Page<PointRecord> getPointRecords(Long userId, int page, int size) {
        return pointRecordRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    /**
     * 系统调整积分（管理员操作）
     * @param userId 用户ID
     * @param points 积分数（可正可负）
     * @param description 调整原因
     * @return 更新后的积分
     */
    @Transactional
    public int adjustPoints(Long userId, int points, String description) {
        return changePoints(userId, points, POINT_TYPE_SYSTEM, null, description);
    }
}