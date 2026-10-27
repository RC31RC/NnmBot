package com.nnm.bot.core.channel.notification.store;

import io.github.rdt.state.SharedStateStore;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** 调度节点与 owner 节点显式共享取消身份；读取失败不允许继续产生平台副作用。 */
@Component
public class NotificationCancellationStore {
    @Resource
    private SharedStateStore states;

    public void cancel(UUID submissionId, Instant expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt).plusMinutes(1);
        states.put("notification-cancel:" + submissionId, true, ttl.isNegative() || ttl.isZero() ? Duration.ofMinutes(1) : ttl);
    }

    public boolean cancelled(UUID submissionId) {
        return states.<Boolean>get("notification-cancel:" + submissionId, Boolean.class).orElse(false);
    }
}
