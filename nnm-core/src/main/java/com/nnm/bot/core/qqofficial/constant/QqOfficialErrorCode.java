package com.nnm.bot.core.qqofficial.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** QQ 官方接口及文件交付的稳定错误码，不回显上游响应或文件名。 */
@Getter
@RequiredArgsConstructor
public enum QqOfficialErrorCode implements ErrorCode {
    GATEWAY_URL_MISSING(502801, "QQ 官方机器人 Gateway 响应缺少 url", HttpStatus.BAD_GATEWAY),
    GROUP_MEMBER_CURSOR_REPEAT(502802, "QQ 官方群成员接口重复返回分页游标", HttpStatus.BAD_GATEWAY),
    GROUP_MEMBERS_MISSING(502803, "QQ 官方群成员接口响应缺少 members", HttpStatus.BAD_GATEWAY),
    GROUP_MEMBER_EMPTY(502804, "QQ 官方群成员接口响应包含空成员", HttpStatus.BAD_GATEWAY),
    GROUP_MEMBER_ID_MISSING(502805, "QQ 官方群成员接口缺少 member_openid", HttpStatus.BAD_GATEWAY),
    GROUP_IDS_REQUIRED(400801, "群组与成员标识不能为空", HttpStatus.BAD_REQUEST),
    GROUP_MEMBER_DETAIL_ID_MISSING(502806, "QQ 官方群成员详情接口缺少 member_openid", HttpStatus.BAD_GATEWAY),
    BUTTON_MARKDOWN_REQUIRED(422801, "QQ 按钮消息需要 Markdown 正文", HttpStatus.UNPROCESSABLE_ENTITY),
    IMAGE_TEMPLATE_UNSUPPORTED(422802, "QQ 图片不能与自定义 Markdown 模板组合", HttpStatus.UNPROCESSABLE_ENTITY),
    MARKDOWN_IMAGE_FORMAT_UNSUPPORTED(500801, "QQ Markdown 不支持图片格式", HttpStatus.INTERNAL_SERVER_ERROR),
    MARKDOWN_IMAGE_READ_FAILED(500802, "读取 QQ Markdown 图片失败", HttpStatus.INTERNAL_SERVER_ERROR),
    MARKDOWN_UPLOAD_URL_INVALID(502807, "QQ Markdown 素材上传地址无效", HttpStatus.BAD_GATEWAY),
    MARKDOWN_UPLOAD_FAILED(500803, "QQ Markdown 图片上传失败", HttpStatus.INTERNAL_SERVER_ERROR),
    MARKDOWN_UPLOAD_DIGEST_INVALID(502808, "QQ Markdown 图片上传摘要不匹配", HttpStatus.BAD_GATEWAY),
    MARKDOWN_IMAGE_REFERENCE_INVALID(502809, "QQ Markdown 图片素材引用无效", HttpStatus.BAD_GATEWAY),
    MARKDOWN_IMAGE_CLEANUP_FAILED(500804, "清理 QQ Markdown 图片素材失败", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_SIZE_READ_FAILED(500805, "读取 QQ 官方机器人待发送文件大小失败", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TOO_LARGE(413801, "QQ 官方机器人文件超过大小限制", HttpStatus.CONTENT_TOO_LARGE),
    FILE_READ_FAILED(500806, "读取 QQ 官方机器人待发送文件失败", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_PARTS_MISSING(502810, "QQ 官方机器人预上传响应缺少有效分片信息", HttpStatus.BAD_GATEWAY),
    FILE_PART_INVALID(502811, "QQ 官方机器人分片响应无效", HttpStatus.BAD_GATEWAY),
    FILE_PART_READ_EARLY(500807, "QQ 官方机器人文件分片读取提前结束", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_PART_LENGTH_MISMATCH(500808, "QQ 官方机器人文件分片长度与预上传响应不一致", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_PART_UPLOAD_FAILED(500809, "QQ 官方机器人分片上传失败", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TOTAL_SIZE_MISMATCH(500810, "QQ 官方机器人文件分片总长度不一致", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_INFO_MISSING(502812, "QQ 官方机器人媒体响应缺少 file_info", HttpStatus.BAD_GATEWAY),
    FILE_PLATFORM_LIMIT_EXCEEDED(413802, "QQ 官方机器人文件超过平台大小限制", HttpStatus.CONTENT_TOO_LARGE),
    MARKDOWN_COMBINATION_UNSUPPORTED(422803, "QQ 仅支持直接 Markdown 与文字、按钮组合", HttpStatus.UNPROCESSABLE_ENTITY),
    BUTTON_COUNT_EXCEEDED(422804, "QQ 按钮不能超过 25 个", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANNEL_RECEIPT_ID_MISSING(502813, "QQ 频道消息响应缺少 id", HttpStatus.BAD_GATEWAY),
    CONNECTION_LEASE_EXPIRED(404801, "机器人连接租约已失效，已阻止发送", HttpStatus.NOT_FOUND),
    TOKEN_MISSING(502814, "QQ 官方机器人 Token 响应缺少 access_token", HttpStatus.BAD_GATEWAY),
    GATEWAY_CHANNEL_NOT_REGISTERED(404802, "未注册 QQ 官方机器人渠道", HttpStatus.NOT_FOUND),
    GATEWAY_PLATFORM_MISMATCH(400802, "QQ 官方客户端不能启动该协议", HttpStatus.BAD_REQUEST),
    GATEWAY_INSTANCE_REQUIRED(400803, "QQ 官方机器人实例记录不能为空", HttpStatus.BAD_REQUEST),
    GATEWAY_INTENTS_INVALID(400804, "QQ 官方机器人 intents 不能为负数", HttpStatus.BAD_REQUEST),
    GATEWAY_TIMEOUT_INVALID(400805, "QQ 官方机器人 Gateway 连接超时必须为正数", HttpStatus.BAD_REQUEST),
    GATEWAY_RECONNECT_INVALID(400806, "QQ 官方机器人重连延迟不能为负数", HttpStatus.BAD_REQUEST),
    GATEWAY_FIELD_REQUIRED(400807, "QQ 官方机器人必填配置不能为空", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
