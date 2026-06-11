package com.blog.user.service;

import com.blog.common.util.JwtUtil;
import com.blog.user.entity.UserProfile;
import com.blog.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用户服务类
 * 提供用户资料的查询和更新功能
 */
@Service
@RequiredArgsConstructor
public class UserService {

    /** 用户资料数据访问层 */
    private final UserProfileRepository userProfileRepository;

    /**
     * 获取用户资料
     * 如果用户资料不存在，自动创建一个默认资料
     * @param userId 用户ID
     * @return 用户资料
     */
    public UserProfile getProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // 如果不存在，创建默认资料
                    UserProfile profile = new UserProfile();
                    profile.setUserId(userId);
                    profile.setNickname("用户" + userId);
                    return userProfileRepository.save(profile);
                });
    }

    /**
     * 更新用户资料
     * @param request 更新请求，包含昵称、头像、简介等字段
     * @param token 用户Token，用于提取用户ID
     * @return 更新后的用户资料
     */
    public UserProfile updateProfile(Map<String, Object> request, String token) {
        // 从Token中提取用户ID
        Long userId = JwtUtil.getUserId(token);
        // 获取当前用户资料
        UserProfile profile = getProfile(userId);

        // 更新指定字段
        if (request.containsKey("nickname")) profile.setNickname((String) request.get("nickname"));
        if (request.containsKey("avatar")) profile.setAvatar((String) request.get("avatar"));
        if (request.containsKey("bio")) profile.setBio((String) request.get("bio"));
        if (request.containsKey("website")) profile.setWebsite((String) request.get("website"));
        if (request.containsKey("location")) profile.setLocation((String) request.get("location"));

        // 保存更新
        return userProfileRepository.save(profile);
    }
}
