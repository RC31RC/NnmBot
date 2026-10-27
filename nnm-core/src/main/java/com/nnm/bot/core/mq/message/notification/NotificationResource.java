package com.nnm.bot.core.mq.message.notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationResource(UUID id, long ownerId, String objectId, String contentType,
                                    long size, int width, int height, Instant expiresAt) { }
