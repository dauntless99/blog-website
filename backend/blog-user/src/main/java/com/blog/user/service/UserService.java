package com.blog.user.service;

import com.blog.common.util.JwtUtil;
import com.blog.user.entity.UserProfile;
import com.blog.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserProfileRepository userProfileRepository;

    public UserProfile getProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile profile = new UserProfile();
                    profile.setUserId(userId);
                    profile.setNickname("用户" + userId);
                    return userProfileRepository.save(profile);
                });
    }

    public UserProfile updateProfile(Map<String, Object> request, String token) {
        Long userId = JwtUtil.getUserId(token);
        UserProfile profile = getProfile(userId);

        if (request.containsKey("nickname")) profile.setNickname((String) request.get("nickname"));
        if (request.containsKey("avatar")) profile.setAvatar((String) request.get("avatar"));
        if (request.containsKey("bio")) profile.setBio((String) request.get("bio"));
        if (request.containsKey("website")) profile.setWebsite((String) request.get("website"));
        if (request.containsKey("location")) profile.setLocation((String) request.get("location"));

        return userProfileRepository.save(profile);
    }
}
