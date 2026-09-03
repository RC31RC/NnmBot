package com.nnm.bot.admin.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BotManagementErrorCode implements ErrorCode {

    ACCOUNT_PROBE_FAILED(502, "无法获取 OneBot 登录账号", HttpStatus.BAD_GATEWAY),
    INSTANCE_START_FAILED(500, "机器人实例启动失败", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
