package com.nnm.bot.admin.error;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BotManagementErrorCode implements ErrorCode {

    ACCOUNT_PROBE_FAILED(502, "无法获取 OneBot 登录账号", HttpStatus.BAD_GATEWAY);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
