package com.blog.user.controller;

import com.blog.common.result.Result;
import com.blog.user.entity.UserProfile;
import com.blog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 * 处理用户资料的查询和更新请求
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    /** 用户服务 */
    private final UserService userService;

    /**
     * 获取用户资料
     * @param userId 用户ID
     * @return 用户资料
     */
    @GetMapping("/profile/{userId}")
    public Result<UserProfile> getProfile(@PathVariable Long userId) {
        return Result.success(userService.getProfile(userId));
    }

    /**
     * 更新用户资料
     * @param request 更新请求体
     * @param token 用户Token（Authorization请求头）
     * @return 更新后的用户资料
     */
    @PutMapping("/profile")
    public Result<UserProfile> updateProfile(@RequestBody Map<String, Object> request,
                                              @RequestHeader("Authorization") String token) {
        try {
            return Result.success("更新成功", userService.updateProfile(request, token));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
