package com.blog.auth.service;

import com.blog.auth.entity.Announcement;
import com.blog.auth.entity.Department;
import com.blog.auth.repository.AnnouncementRepository;
import com.blog.auth.repository.DepartmentRepository;
import com.blog.common.entity.Role;
import com.blog.common.entity.User;
import com.blog.common.entity.UserLevel;
import com.blog.common.repository.RoleRepository;
import com.blog.common.repository.UserLevelRepository;
import com.blog.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员服务类
 * 提供管理员控制台、数据统计、用户管理等功能
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserLevelRepository userLevelRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final AnnouncementRepository announcementRepository;

    /**
     * 获取系统统计数据
     * @return 统计数据
     */
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        long totalUsers = userRepository.count();
        long todayUsers = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(1));
        stats.put("totalUsers", totalUsers);
        stats.put("todayUsers", todayUsers);
        
        // 部门统计
        long totalDepartments = departmentRepository.count();
        stats.put("totalDepartments", totalDepartments);
        
        // 角色统计
        long totalRoles = roleRepository.count();
        stats.put("totalRoles", totalRoles);
        
        // 用户等级统计
        long totalLevels = userLevelRepository.count();
        stats.put("totalLevels", totalLevels);
        
        // 公告统计
        long totalAnnouncements = announcementRepository.count();
        stats.put("totalAnnouncements", totalAnnouncements);
        
        return stats;
    }

    /**
     * 获取用户列表（管理员查看）
     * @param page 页码
     * @param size 每页大小
     * @return 分页用户列表
     */
    public Page<User> getUserList(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * 获取用户等级列表
     * @return 用户等级列表
     */
    public Page<UserLevel> getUserLevelList(int page, int size) {
        return userLevelRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * 获取角色列表
     * @return 角色列表
     */
    public Page<Role> getRoleList(int page, int size) {
        return roleRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * 获取部门列表
     * @return 部门列表
     */
    public Page<Department> getDepartmentList(int page, int size) {
        return departmentRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * 获取公告列表
     * @return 公告列表
     */
    public Page<Announcement> getAnnouncementList(int page, int size) {
        return announcementRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }
}