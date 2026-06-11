package com.blog.auth.repository;

import com.blog.auth.entity.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户等级数据访问层
 */
@Repository
public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {

    /**
     * 根据积分获取用户等级
     * @param points 当前积分
     * @return 用户等级
     */
    @Query("SELECT u FROM UserLevel u WHERE u.minPoints <= :points ORDER BY u.minPoints DESC LIMIT 1")
    Optional<UserLevel> findLevelByPoints(@Param("points") Integer points);

    /**
     * 根据等级名称查找等级
     * @param name 等级名称
     * @return 用户等级
     */
    Optional<UserLevel> findByName(String name);

    /**
     * 按所需积分升序获取所有等级
     * @return 等级列表
     */
    java.util.List<UserLevel> findAllByOrderByMinPointsAsc();
}