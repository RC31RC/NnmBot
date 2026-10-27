package com.nnm.bot.core.mq.message.notification;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** 提交时复制为任务专属对象，后续删除草稿不会破坏在途任务。 */
public record NotificationResourceManifest(UUID submissionId, Map<UUID, NotificationResource> resources,
                                            Instant expiresAt) {
    public NotificationResourceManifest {
        resources = Map.copyOf(resources);
    }
}
