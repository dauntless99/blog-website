package com.blog.auth.repository;

import com.blog.auth.entity.PointRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 积分记录数据访问层
 */
@Repository
public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {

    /**
     * 根据用户ID分页查询积分记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 积分记录分页
     */
    Page<PointRecord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 根据用户ID和类型查询积分记录
     * @param userId 用户ID
     * @param type 变动类型
     * @return 积分记录列表
     */
    List<PointRecord> findByUserIdAndType(Long userId, String type);

    /**
     * 统计用户总积分（通过变动记录计算）
     * @param userId 用户ID
     * @return 总积分变化
     */
    default int sumPointsByUserId(Long userId) {
        return findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .getContent()
                .stream()
                .mapToInt(PointRecord::getPoints)
                .sum();
    }
}