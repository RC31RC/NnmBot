package com.nnm.bot.admin.dto.auth;

/** 登录成功后返回的令牌。 */
public record LoginResponse(
        /** 访问令牌。 */
        String token,
        /** 刷新令牌。 */
        String refreshToken
) {
}
