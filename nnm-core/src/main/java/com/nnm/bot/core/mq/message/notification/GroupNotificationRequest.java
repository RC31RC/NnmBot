package com.nnm.bot.core.mq.message.notification;

import com.nnm.bot.core.message.model.BotProactiveTarget;
import java.time.Instant;
import java.util.UUID;

/** Explicit delivery identity; never carries an incoming message context or credentials. */
public record GroupNotificationRequest(UUID requestId, BotProactiveTarget target, NotificationContent content,
                                       NotificationResourceManifest resources, Instant expiresAt) { }
