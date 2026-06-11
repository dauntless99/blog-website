package com.blog.auth.repository;

import com.blog.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 角色数据访问层
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据角色名称查找角色
     * @param name 角色名称
     * @return 角色实体
     */
    Optional<Role> findByName(String name);
}