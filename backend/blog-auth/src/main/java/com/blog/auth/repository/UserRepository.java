package com.blog.auth.repository;

import com.blog.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户数据访问层
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象（可选）
     */
    Optional<User> findByUsername(String username);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据创建时间统计用户数量
     * @param dateTime 时间点
     * @return 用户数量
     */
    long countByCreatedAtAfter(LocalDateTime dateTime);
}
