package com.nnm.bot.admin.pojo.dto.auth;

/** 登录成功或续签后返回的令牌。 */
public record LoginTokenDTO(
        /** 访问令牌。 */
        String token
) {
}
