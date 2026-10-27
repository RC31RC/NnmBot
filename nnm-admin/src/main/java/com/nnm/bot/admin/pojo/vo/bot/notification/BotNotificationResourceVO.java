package com.nnm.bot.admin.pojo.vo.bot.notification;

import java.time.Instant;
import java.util.UUID;

/** 管理端只暴露编辑器需要的资源元数据，不泄露对象存储键和所有者身份。 */
public record BotNotificationResourceVO(UUID id, String contentType, long size, int width, int height,
                                          Instant expiresAt) {
}
