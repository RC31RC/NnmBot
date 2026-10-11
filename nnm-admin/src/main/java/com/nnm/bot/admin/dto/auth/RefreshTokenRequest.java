package com.nnm.bot.admin.dto.auth;

import jakarta.validation.constraints.NotBlank;

/** 使用当前 Sa-Token 令牌续签请求。 */
public record RefreshTokenRequest(
        /** 当前访问令牌；普通 Sa-Token 模式不再使用独立刷新令牌。 */
        @NotBlank(message = "登录令牌不能为空")
        String token
) {
}
