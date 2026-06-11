package com.blog.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /** 用户名（必填，3-20个字符） */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度3-20个字符")
    private String username;

    /** 密码（必填，6-30个字符） */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度6-30个字符")
    private String password;

    /** 邮箱（可选，需符合邮箱格式） */
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 昵称（可选） */
    private String nickname;
}
