package com.nnm.bot.admin.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 管理端登录、会话和账号状态相关的稳定错误码。 */
@Getter
@RequiredArgsConstructor
public enum AdminAuthErrorCode implements ErrorCode {
    INVALID_CREDENTIALS(401101, "用户名或密码错误", HttpStatus.UNAUTHORIZED),
    ACCOUNT_UNAVAILABLE(401102, "账号不可用", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(401103, "登录令牌无效", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
