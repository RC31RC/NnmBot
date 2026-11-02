package com.nnm.bot.core.mq.message.notification;

import com.nnm.bot.core.message.model.BotProactiveTarget;
import java.time.Instant;
import java.util.UUID;

/** 显式传递投递身份，不携带入站消息上下文或凭据。 */
public record GroupNotificationRequest(UUID requestId, BotProactiveTarget target, NotificationContent content,
    NotificationResourceManifest resources, Instant expiresAt) {
}
