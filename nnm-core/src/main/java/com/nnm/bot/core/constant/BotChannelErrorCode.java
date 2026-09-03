package com.nnm.bot.core.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 渠道运行时错误码，供管理接口和渠道适配层共享。 */
@Getter
@RequiredArgsConstructor
public enum BotChannelErrorCode implements ErrorCode {

    INSTANCE_NOT_FOUND(404, "机器人实例不存在", HttpStatus.NOT_FOUND),
    INSTANCE_DISABLED(409, "机器人实例已禁用", HttpStatus.CONFLICT),
    INSTANCE_CONFIG_INVALID(400, "机器人实例连接配置无效", HttpStatus.BAD_REQUEST),
    PLATFORM_MISMATCH(400, "机器人实例协议不匹配", HttpStatus.BAD_REQUEST),
    INSTANCE_START_FAILED(502, "机器人实例启动失败", HttpStatus.BAD_GATEWAY),
    CHANNEL_NOT_REGISTERED(404, "机器人渠道未注册", HttpStatus.NOT_FOUND),
    CHANNEL_REGISTRATION_CONFLICT(409, "机器人渠道注册冲突", HttpStatus.CONFLICT),
    ACCOUNT_NOT_ALLOWED(403, "机器人账号未授权", HttpStatus.FORBIDDEN),
    INVALID_ACCOUNT_ID(400, "机器人账号标识无效", HttpStatus.BAD_REQUEST),
    MESSAGE_PARSE_FAILED(502, "渠道消息解析失败", HttpStatus.BAD_GATEWAY),
    MESSAGE_SOURCE_INVALID(400, "消息来源类型不匹配", HttpStatus.BAD_REQUEST),
    SCOPE_DISCOVERY_FAILED(502, "渠道群组发现失败", HttpStatus.BAD_GATEWAY),
    SCOPE_MEMBER_DISCOVERY_FAILED(502, "渠道群成员发现失败", HttpStatus.BAD_GATEWAY),
    MESSAGE_SENDER_NOT_REGISTERED(500, "消息发送器未注册", HttpStatus.INTERNAL_SERVER_ERROR),
    MESSAGE_SEND_FAILED(502, "机器人消息发送失败", HttpStatus.BAD_GATEWAY),
    FILE_STAGING_FAILED(500, "机器人文件暂存失败", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_CLEANUP_FAILED(500, "机器人临时文件清理失败", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_RENDER_FAILED(500, "命令图片渲染失败", HttpStatus.INTERNAL_SERVER_ERROR),
    RESPONSE_STRATEGY_NOT_REGISTERED(500, "机器人响应策略未注册", HttpStatus.INTERNAL_SERVER_ERROR),
    UNSUPPORTED_MESSAGE_CAPABILITY(501, "当前机器人渠道不支持该消息能力", HttpStatus.NOT_IMPLEMENTED),
    UPSTREAM_RESPONSE_INVALID(502, "渠道上游响应无效", HttpStatus.BAD_GATEWAY);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
