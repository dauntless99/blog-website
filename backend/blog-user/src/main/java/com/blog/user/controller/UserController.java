package com.blog.user.controller;

import com.blog.common.result.Result;
import com.blog.user.entity.UserProfile;
import com.blog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{userId}")
    public Result<UserProfile> getProfile(@PathVariable Long userId) {
        return Result.success(userService.getProfile(userId));
    }

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
