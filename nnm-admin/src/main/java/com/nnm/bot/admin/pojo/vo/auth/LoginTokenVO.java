package com.nnm.bot.admin.pojo.vo.auth;

/** 登录成功或续签后返回的令牌。 */
public record LoginTokenVO(
        /** 访问令牌。 */
        String token
) {
}
