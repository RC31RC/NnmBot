package com.nnm.bot.admin.constant.bot;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 机器人实例、作用域、命令及群组管理错误码。 */
@Getter
@RequiredArgsConstructor
public enum BotManagementErrorCode implements ErrorCode {

    ACCOUNT_PROBE_FAILED(502, "无法获取 OneBot 登录账号", HttpStatus.BAD_GATEWAY),
    INSTANCE_START_FAILED(500, "机器人实例启动失败", HttpStatus.INTERNAL_SERVER_ERROR),
    CREDENTIAL_ENCRYPT_FAILED(500301, "机器人连接凭据加密失败", HttpStatus.INTERNAL_SERVER_ERROR),
    CREDENTIAL_FORMAT_INVALID(500302, "机器人连接凭据不是受支持的密文格式", HttpStatus.INTERNAL_SERVER_ERROR),
    CREDENTIAL_DECRYPT_FAILED(500303, "机器人连接凭据解密失败", HttpStatus.INTERNAL_SERVER_ERROR),
    ROBOT_ROLE_CODE_CONFLICT(409401, "机器人角色编码已存在", HttpStatus.CONFLICT),
    ROBOT_ROLE_UNKNOWN(400401, "包含不存在的机器人角色", HttpStatus.BAD_REQUEST),
    ROBOT_ROLE_IN_USE(409402, "机器人角色仍绑定机器人实例，不能删除", HttpStatus.CONFLICT),
    ROBOT_ROLE_INSTANCE_INVALID(400402, "机器人角色包含重复或不存在的机器人实例", HttpStatus.BAD_REQUEST),
    ROBOT_ROLE_COMMAND_UNKNOWN(400403, "机器人角色包含不存在的命令", HttpStatus.BAD_REQUEST),
    GROUP_CODE_CONFLICT(409403, "群组编码已存在", HttpStatus.CONFLICT),
    GROUP_INSTANCE_PLATFORM_MISMATCH(400404, "机器人实例平台与群组绑定平台不一致", HttpStatus.BAD_REQUEST),
    GROUP_MEMBER_ROLE_INVALID(400405, "群组成员角色不合法", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_FOUND(404401, "通知任务不存在", HttpStatus.NOT_FOUND),
    SCOPE_PLATFORM_MISMATCH(400406, "机器人实例平台与作用域平台不一致", HttpStatus.BAD_REQUEST),
    SCOPE_CONFLICT(409404, "该机器人已经配置了相同的群组作用域", HttpStatus.CONFLICT),
    INSTANCE_ID_CONFLICT(409405, "实例标识已存在", HttpStatus.CONFLICT),
    INSTANCE_DISABLED(409406, "机器人实例已禁用，请先编辑并启用实例", HttpStatus.CONFLICT),
    ACCOUNT_CONFLICT(409407, "机器人账号已存在", HttpStatus.CONFLICT),
    ONEBOT_URL_REQUIRED(400407, "OneBot WebSocket 地址不能为空", HttpStatus.BAD_REQUEST),
    QQ_SECRET_REQUIRED(400408, "新增 QQ 官方机器人必须填写 Client Secret", HttpStatus.BAD_REQUEST),
    INSTANCE_PLATFORM_MISMATCH(400409, "机器人实例协议不匹配", HttpStatus.BAD_REQUEST),
    TEMPLATE_CODE_CONFLICT(409408, "模板编码已存在", HttpStatus.CONFLICT),
    TEMPLATE_IN_USE(409409, "模板仍被命令选用，不能删除", HttpStatus.CONFLICT),
    HTML_TEMPLATE_REQUIRED(400410, "请提供 HTML 模板", HttpStatus.BAD_REQUEST),
    COMMAND_ROLE_UNKNOWN(400411, "命令角色包含不存在的角色", HttpStatus.BAD_REQUEST),
    COMMAND_SYSTEM_ROLE_UNKNOWN(400412, "命令系统角色包含不存在的角色", HttpStatus.BAD_REQUEST),
    GROUP_ROLE_INVALID(400413, "群组角色要求只能是 OWNER 或 ADMIN", HttpStatus.BAD_REQUEST),
    TEMPLATE_DISABLED(400414, "不能选择已停用的 HTML 模板", HttpStatus.BAD_REQUEST),
    TEMPLATE_COMMAND_MISMATCH(400415, "只能选择归属于当前命令的 HTML 模板", HttpStatus.BAD_REQUEST),
    GROUP_SCOPE_ALREADY_BOUND(409410, "该机器人作用域已经绑定到逻辑群组", HttpStatus.CONFLICT),
    GROUP_SELECTION_INVALID(400416, "请选择 1–200 个群组", HttpStatus.BAD_REQUEST),
    GROUP_SELECTION_MISSING(400417, "部分群组已不存在", HttpStatus.BAD_REQUEST),
    SUBMISSION_CONFLICT(400418, "同一提交标识不能用于不同通知内容", HttpStatus.BAD_REQUEST),
    SCHEDULE_IN_PAST(400419, "定时发送时间必须晚于当前时间", HttpStatus.BAD_REQUEST),
    SCHEDULE_OUT_OF_RANGE(400420, "定时发送时间超出允许范围", HttpStatus.BAD_REQUEST),
    NOTIFICATION_CAPACITY_REACHED(400421, "待发送通知任务数量已达上限", HttpStatus.BAD_REQUEST),
    NOTIFICATION_FORBIDDEN(400422, "无权操作该通知任务", HttpStatus.BAD_REQUEST),
    SUBMISSION_ID_REQUIRED(400423, "缺少通知提交标识", HttpStatus.BAD_REQUEST),
    INSTANCE_NOT_FOUND(404402, "机器人实例不存在", HttpStatus.NOT_FOUND),
    ROBOT_ROLE_NOT_FOUND(404403, "机器人角色不存在", HttpStatus.NOT_FOUND),
    GROUP_INSTANCE_BINDING_NOT_FOUND(404404, "群组机器人绑定不存在", HttpStatus.NOT_FOUND),
    GROUP_MEMBER_NOT_FOUND(404405, "群组成员不存在", HttpStatus.NOT_FOUND),
    GROUP_NOT_FOUND(404406, "逻辑群组不存在", HttpStatus.NOT_FOUND),
    GROUP_SCOPE_NOT_FOUND(404407, "群组作用域不存在", HttpStatus.NOT_FOUND),
    TEMPLATE_NOT_FOUND(404408, "模板不存在", HttpStatus.NOT_FOUND),
    TEMPLATE_COMMAND_NOT_FOUND(404409, "所属命令不存在", HttpStatus.NOT_FOUND),
    PREVIOUS_TEMPLATE_COMMAND_NOT_FOUND(404410, "原所属命令不存在", HttpStatus.NOT_FOUND),
    COMMAND_NOT_FOUND(404411, "命令不存在", HttpStatus.NOT_FOUND),
    COMMAND_TEMPLATE_NOT_FOUND(404412, "命令模板不存在", HttpStatus.NOT_FOUND),
    MULTIPLE_HTML_TEMPLATES(400424, "每个命令只能选择一个 HTML 模板", HttpStatus.BAD_REQUEST),
    TEMPLATE_HTML_REQUIRED(400425, "命令模板必须提供 HTML 内容", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
