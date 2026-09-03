package com.nnm.bot.admin.dto.auth;

import jakarta.validation.constraints.NotBlank;

/** 刷新登录令牌请求。 */
public record RefreshTokenRequest(
        /** 用于换取新访问令牌的刷新令牌。 */
        @NotBlank(message = "刷新令牌不能为空") String refreshToken
) {
}
