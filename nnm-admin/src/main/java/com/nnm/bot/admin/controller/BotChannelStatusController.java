package com.nnm.bot.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.dto.bot.BotChannelStatusEvent;
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
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/** 管理端机器人实例运行状态的 SSE 订阅入口。 */
@Slf4j
@RestController
@RequestMapping("/api/admin/v1/bot-instances")
public class BotChannelStatusController {

    @Resource
    private BotChannelRegistry registry;
    @Resource(name = "taskScheduler")
    private ThreadPoolTaskScheduler taskScheduler;

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SaCheckPermission("bot:instance:view")
    public SseEmitter events() {
        SseEmitter emitter = new SseEmitter(0L);
        Object sendLock = new Object();
        AtomicBoolean closed = new AtomicBoolean();
        AtomicReference<BotChannelRegistry.Subscription> subscription =
                new AtomicReference<>();
        AtomicReference<ScheduledFuture<?>> keepAlive = new AtomicReference<>();

        // 完成、超时和发送异常可能并发发生，清理动作必须只执行一次。
        Runnable cleanup = () -> {
            if (!closed.compareAndSet(false, true)) {
                return;
            }
            ScheduledFuture<?> task = keepAlive.getAndSet(null);
            if (task != null) {
                task.cancel(false);
            }
            BotChannelRegistry.Subscription value = subscription.getAndSet(null);
            if (value != null) {
                value.close();
            }
        };
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ignored -> cleanup.run());

        ScheduledFuture<?> scheduledKeepAlive = taskScheduler.scheduleAtFixedRate(
                () -> sendKeepAlive(emitter, sendLock, cleanup),
                Duration.ofSeconds(25));
        keepAlive.set(scheduledKeepAlive);
        if (closed.get() && keepAlive.compareAndSet(scheduledKeepAlive, null)) {
            scheduledKeepAlive.cancel(false);
        }

        BotChannelRegistry.Subscription registered = registry.subscribe(snapshot ->
                sendSnapshot(emitter, sendLock, cleanup, snapshot));
        subscription.set(registered);
        if (closed.get() && subscription.compareAndSet(registered, null)) {
            registered.close();
        }

        // 先推送当前快照，页面打开时无需等待下一次连接状态变化。
        registry.snapshots().forEach(snapshot ->
                sendSnapshot(emitter, sendLock, cleanup, snapshot));
        return emitter;
    }

    private void sendSnapshot(
            SseEmitter emitter,
            Object sendLock,
            Runnable cleanup,
            BotChannelSnapshot snapshot) {
        BotChannelStatusEvent event = new BotChannelStatusEvent(
                snapshot.instanceId(),
                snapshot.key().platform(),
                snapshot.key().botAccountId(),
                snapshot.status(),
                snapshot.statusChangedAt(),
                snapshot.lastError());
        try {
            synchronized (sendLock) {
                emitter.send(SseEmitter.event()
                        .name("status")
                        .data(event));
            }
        } catch (IOException | RuntimeException exception) {
            log.debug("机器人实例状态 SSE 发送失败", exception);
            cleanup.run();
            emitter.complete();
        }
    }

    private void sendKeepAlive(
            SseEmitter emitter,
            Object sendLock,
            Runnable cleanup) {
        try {
            synchronized (sendLock) {
                emitter.send(SseEmitter.event()
                        .name("keepalive")
                        .data("ping"));
            }
        } catch (IOException | RuntimeException exception) {
            log.debug("机器人实例状态 SSE 保活发送失败", exception);
            cleanup.run();
            emitter.complete();
        }
    }
}
