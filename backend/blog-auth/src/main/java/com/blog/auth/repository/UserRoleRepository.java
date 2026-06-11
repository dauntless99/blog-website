package com.blog.auth.repository;

import com.blog.auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色关联数据访问层
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * 根据用户ID查找所有角色关联
     * @param userId 用户ID
     * @return 角色关联列表
     */
    List<UserRole> findByUserId(Long userId);

    /**
     * 根据用户ID删除所有角色关联
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);

    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否拥有该角色
     */
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
}