package com.nnm.bot.admin.dto.auth;

import jakarta.validation.constraints.NotBlank;

/** 后台登录请求。 */
public record LoginRequest(
        /** 登录用户名。 */
        @NotBlank(message = "用户名不能为空") String userName,
        /** 登录密码。 */
        @NotBlank(message = "密码不能为空") String password
) {
}
