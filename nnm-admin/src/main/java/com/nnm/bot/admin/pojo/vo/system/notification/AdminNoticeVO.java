package com.nnm.bot.admin.pojo.vo.system.notification;

import java.time.Instant;

/** 当前管理员可查看的持久化消息。 */
public record AdminNoticeVO(String id, String title, String content, String taskId, String taskType, String taskStatus,
    String previewId, String previewDataset, Instant createdAt, Instant expiresAt) {
}
