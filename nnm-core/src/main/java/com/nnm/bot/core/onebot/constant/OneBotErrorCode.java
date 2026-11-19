package com.nnm.bot.core.onebot.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** OneBot 连接及自报账号的稳定验证错误。 */
@Getter
@RequiredArgsConstructor
public enum OneBotErrorCode implements ErrorCode {
    PLATFORM_MISMATCH(400851, "OneBot 客户端不能启动该协议", HttpStatus.BAD_REQUEST),
    SELF_ACCOUNT_INVALID(400852, "OneBot self_id 不是有效的 QQ 账号", HttpStatus.BAD_REQUEST),
    SELF_ACCOUNT_MISSING(502851, "OneBot self_id 不能为空", HttpStatus.BAD_GATEWAY),
    MESSAGE_JSON_INVALID(502852, "OneBot 消息不是合法 JSON", HttpStatus.BAD_GATEWAY),
    SELF_ACCOUNT_PARSE_FAILED(502853, "解析 OneBot 消息中的 self_id 失败", HttpStatus.BAD_GATEWAY),
    ACCOUNT_NOT_AUTHORIZED(403851, "OneBot 连接上报了未授权的机器人账号", HttpStatus.FORBIDDEN),
    INSTANCE_REQUIRED(400853, "OneBot 机器人实例记录不能为空", HttpStatus.BAD_REQUEST),
    INSTANCE_ID_REQUIRED(400854, "OneBot 机器人实例标识不能为空", HttpStatus.BAD_REQUEST),
    INSTANCE_ACCOUNT_REQUIRED(400855, "OneBot 机器人账号不能为空", HttpStatus.BAD_REQUEST),
    CONNECTION_URL_REQUIRED(400856, "OneBot WebSocket 地址不能为空", HttpStatus.BAD_REQUEST),
    RECONNECT_INTERVAL_INVALID(400857, "OneBot 重连间隔必须为正数", HttpStatus.BAD_REQUEST),
    INSTANCE_ACCOUNT_INVALID(400858, "OneBot 账号不是有效的 QQ 账号", HttpStatus.BAD_REQUEST),
    PROBE_URL_REQUIRED(400859, "OneBot WebSocket 地址不能为空", HttpStatus.BAD_REQUEST),
    PROBE_URL_INVALID(400860, "OneBot WebSocket 地址格式不合法", HttpStatus.BAD_REQUEST),
    PROBE_URL_SCHEME_INVALID(400861, "OneBot WebSocket 地址必须使用 ws 或 wss 协议", HttpStatus.BAD_REQUEST),
    PROBE_URL_HOST_INVALID(400862, "OneBot WebSocket 地址缺少有效主机名", HttpStatus.BAD_REQUEST),
    DISCOVERY_ACCOUNT_INVALID(400863, "OneBot 账号不是有效的 QQ 号", HttpStatus.BAD_REQUEST),
    DISCOVERY_FAILED(502854, "OneBot 群组查询失败", HttpStatus.BAD_GATEWAY),
    PROACTIVE_IMAGE_READ_FAILED(500851, "读取主动消息图片失败", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
