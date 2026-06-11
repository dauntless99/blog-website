package com.blog.auth.service;

import com.blog.auth.entity.Role;
import com.blog.auth.entity.UserLevel;
import com.blog.auth.repository.RoleRepository;
import com.blog.auth.repository.UserLevelRepository;
import com.blog.auth.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限服务类
 * 提供角色管理、权限验证等功能
 */
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserLevelRepository userLevelRepository;

    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleName 角色名称
     * @return 是否拥有该角色
     */
    public boolean hasRole(Long userId, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElse(null);
        if (role == null) {
            return false;
        }
        return userRoleRepository.existsByUserIdAndRoleId(userId, role.getId());
    }

    /**
     * 检查用户是否是管理员
     * @param userId 用户ID
     * @return 是否是管理员
     */
    public boolean isAdmin(Long userId) {
        return hasRole(userId, "ADMIN");
    }

    /**
     * 检查用户是否是版主
     * @param userId 用户ID
     * @return 是否是版主
     */
    public boolean isModerator(Long userId) {
        return hasRole(userId, "MODERATOR");
    }

    /**
     * 获取用户的所有角色
     * @param userId 用户ID
     * @return 角色名称集合
     */
    public Set<String> getUserRoles(Long userId) {
        return userRoleRepository.findByUserId(userId).stream()
                .map(ur -> roleRepository.findById(ur.getRoleId())
                        .map(Role::getName)
                        .orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toSet());
    }

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleName 角色名称
     */
    @Transactional
    public void assignRole(Long userId, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        if (!userRoleRepository.existsByUserIdAndRoleId(userId, role.getId())) {
            com.blog.auth.entity.UserRole userRole = new com.blog.auth.entity.UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(role.getId());
            userRoleRepository.save(userRole);
        }
    }

    /**
     * 移除用户的角色
     * @param userId 用户ID
     * @param roleName 角色名称
     */
    @Transactional
    public void removeRole(Long userId, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        userRoleRepository.findByUserId(userId).stream()
                .filter(ur -> ur.getRoleId().equals(role.getId()))
                .findFirst()
                .ifPresent(userRoleRepository::delete);
    }

    /**
     * 获取用户当前等级
     * @param points 当前积分
     * @return 用户等级
     */
    public UserLevel getUserLevel(Integer points) {
        return userLevelRepository.findLevelByPoints(points)
                .orElseGet(() -> {
                    // 返回最低等级或默认等级
                    return userLevelRepository.findAllByOrderByMinPointsAsc()
                            .stream()
                            .findFirst()
                            .orElse(null);
                });
    }

    /**
     * 获取所有等级列表
     * @return 等级列表
     */
    public List<UserLevel> getAllLevels() {
        return userLevelRepository.findAllByOrderByMinPointsAsc();
    }

    /**
     * 检查用户是否有权限进行操作
     * @param userId 用户ID
     * @param requiredRoles 所需角色列表（任意一个即可）
     * @return 是否有权限
     */
    public boolean hasAnyRole(Long userId, List<String> requiredRoles) {
        Set<String> userRoles = getUserRoles(userId);
        return requiredRoles.stream().anyMatch(userRoles::contains);
    }
}