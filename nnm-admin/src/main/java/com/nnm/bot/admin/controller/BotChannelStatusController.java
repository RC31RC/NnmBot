package com.nnm.bot.admin.controller;

import com.nnm.bot.core.channel.registry.BotChannelRegistry.Subscription;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.pojo.vo.bot.channel.BotChannelStatusVO;
import com.nnm.bot.admin.service.bot.channel.BotChannelStatusService;
import com.nnm.bot.admin.event.channel.BotChannelStatusBroadcaster;
import com.nnm.bot.core.config.BotCacheProperties;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/** 管理端机器人实例运行状态的 SSE 订阅入口。 */
@Slf4j
@RestController
@RequestMapping("/api/admin/v1/bot-instances")
public class BotChannelStatusController {

    @Resource
    private BotChannelRegistry registry;
    @Resource(name = "taskScheduler")
    private ThreadPoolTaskScheduler taskScheduler;
    @Resource
    private BotChannelStatusService statusService;
    @Resource
    private BotChannelStatusBroadcaster broadcaster;
    @Resource
    private BotCacheProperties cacheProperties;

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SaCheckPermission("bot:instance:view")
    public SseEmitter events() {
        SseEmitter emitter = new SseEmitter(0L);
        ReentrantLock sendLock = new ReentrantLock();
        AtomicBoolean closed = new AtomicBoolean();
        AtomicReference<Subscription> subscription =
                new AtomicReference<>();
        AtomicReference<ScheduledFuture<?>> keepAlive = new AtomicReference<>();
        Map<String, BotChannelSnapshot> sentSnapshots = new HashMap<>();

        // 完成、超时和发送异常可能并发发生，清理动作必须只执行一次。
        Runnable cleanup = () -> {
            if (!closed.compareAndSet(false, true)) {
                return;
            }
            ScheduledFuture<?> task = keepAlive.getAndSet(null);
            if (task != null) {
                task.cancel(false);
            }
            Subscription value = subscription.getAndSet(null);
            if (value != null) {
                value.close();
            }
        };
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ignored -> cleanup.run());

        try {
            ScheduledFuture<?> scheduledKeepAlive = taskScheduler.scheduleAtFixedRate(
                    () -> sendKeepAlive(emitter, sendLock, cleanup),
                    cacheProperties.statusHeartbeat());
            keepAlive.set(scheduledKeepAlive);
            if (closed.get() && keepAlive.compareAndSet(scheduledKeepAlive, null)) {
                scheduledKeepAlive.cancel(false);
            }

            Subscription registered = broadcaster.subscribe(
                    snapshot -> sendSnapshotIfChanged(emitter, sendLock, cleanup, sentSnapshots, snapshot),
                    failure -> {
                        cleanup.run();
                        emitter.completeWithError(failure);
                    });
            subscription.set(registered);
            if (closed.get() && subscription.compareAndSet(registered, null)) registered.close();
        } catch (RuntimeException exception) {
            cleanup.run();
            emitter.completeWithError(exception);
        }
        return emitter;
    }

    private void sendSnapshotIfChanged(
            SseEmitter emitter,
            ReentrantLock sendLock,
            Runnable cleanup,
            Map<String, BotChannelSnapshot> sentSnapshots,
            BotChannelSnapshot snapshot) {
        sendLock.lock();
        try {
            BotChannelSnapshot previous = sentSnapshots.get(snapshot.instanceId());
            if (sameSnapshot(previous, snapshot)) {
                return;
            }
            if (sendSnapshotLocked(emitter, cleanup, snapshot)) {
                sentSnapshots.put(snapshot.instanceId(), snapshot);
            }
        } finally {
            sendLock.unlock();
        }
    }

    private boolean sendSnapshotLocked(
            SseEmitter emitter,
            Runnable cleanup,
            BotChannelSnapshot snapshot) {
        BotChannelStatusVO event = new BotChannelStatusVO(
                snapshot.instanceId(),
                snapshot.key().platform(),
                snapshot.key().botAccountId(),
                snapshot.status(),
                snapshot.statusChangedAt(),
                snapshot.lastError());
        try {
            emitter.send(SseEmitter.event()
                    .name("status")
                    .data(event));
            return true;
        } catch (IOException | RuntimeException exception) {
            log.debug("机器人实例状态 SSE 发送失败", exception);
            cleanup.run();
            emitter.complete();
            return false;
        }
    }

    private boolean sameSnapshot(BotChannelSnapshot left, BotChannelSnapshot right) {
        return left != null
                && Objects.equals(left.instanceId(), right.instanceId())
                && Objects.equals(left.key(), right.key())
                && left.status() == right.status()
                && Objects.equals(left.statusChangedAt(), right.statusChangedAt())
                && Objects.equals(left.lastError(), right.lastError());
    }

    private void sendKeepAlive(
            SseEmitter emitter,
            ReentrantLock sendLock,
            Runnable cleanup) {
        try {
            sendLock.lock();
            try {
                emitter.send(SseEmitter.event()
                        .name("keepalive")
                        .data("ping"));
            } finally {
                sendLock.unlock();
            }
        } catch (IOException | RuntimeException exception) {
            log.debug("机器人实例状态 SSE 保活发送失败", exception);
            cleanup.run();
            emitter.complete();
        }
    }
}
