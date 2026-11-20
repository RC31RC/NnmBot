package com.nnm.bot.core.mq.message.notification;

import java.time.Instant;
import java.util.UUID;

/**
 * 通知消息引用的附件及其受限预览元数据。
 *
 * @param id 附件记录标识
 * @param ownerId 资源所有者
 * @param objectId 对象存储键
 * @param contentType MIME 类型
 * @param size 文件字节数
 * @param width 图片宽度；非图片为 0
 * @param height 图片高度；非图片为 0
 * @param expiresAt 资源过期时点
 */
public record NotificationResource(UUID id, long ownerId, String objectId, String contentType, long size, int width,
    int height, Instant expiresAt) {
}
