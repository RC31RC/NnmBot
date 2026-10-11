package com.nnm.bot.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.exception.DataNotFoundException;
import com.nnm.bot.common.task.AdminTaskEvent;
import com.nnm.bot.common.task.AdminTaskService;
import com.nnm.bot.common.task.AdminTaskStatus;
import com.nnm.bot.common.web.EmptyRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ScheduledFuture;

/**
 * 管理端长任务的状态查询与 SSE 订阅入口。
 *
 * <p>任务创建由各业务 Controller 完成；这里统一校验任务权限、转发状态事件并管理
 * SSE 连接生命周期，不执行具体刷新业务。</p>
 */
@RestController
@RequestMapping("/api/admin/v1/tasks")
@Slf4j
public class AdminTaskController {

    @Resource
    private AdminTaskService taskService;
    @Resource(name = "taskScheduler")
    private ThreadPoolTaskScheduler taskScheduler;

    @PostMapping("/{taskId}")
    public ApiResponse<AdminTaskEvent> get(
            @PathVariable
            UUID taskId,
            @RequestBody(required = false)
            EmptyRequest ignored) {
        AdminTaskEvent event = findAuthorized(taskId);
        return ApiResponse.ok(event);
    }

    @GetMapping(
            value = "/{taskId}/events",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events(
            @PathVariable
            UUID taskId) {
        findAuthorized(taskId);
        SseEmitter emitter = new SseEmitter(0L);
        log.info("管理端 SSE 建立, taskId={}", taskId);
        AtomicReference<AdminTaskService.TaskSubscription> subscription =
                new AtomicReference<>();
        AtomicReference<ScheduledFuture<?>> keepAlive = new AtomicReference<>();
        Object sendLock = new Object();
        AtomicBoolean closed = new AtomicBoolean();
        Runnable cleanup = () -> {
            if (closed.compareAndSet(false, true)) {
                log.info("管理端 SSE 关闭, taskId={}", taskId);
            }
            close(subscription, keepAlive);
        };
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ignored -> cleanup.run());

        try {
            ScheduledFuture<?> keepAliveTask = taskScheduler.scheduleAtFixedRate(
                    () -> sendKeepAlive(emitter, taskId, sendLock, cleanup),
                    Duration.ofSeconds(25));
            keepAlive.set(keepAliveTask);
            if (closed.get()) cleanup.run();

            AdminTaskService.TaskSubscription registered = taskService.subscribe(taskId, event -> {
                boolean sent = send(
                        emitter,
                        taskId,
                        sendLock,
                        SseEmitter.event()
                                .id(String.valueOf(event.sequence()))
                                .name(event.status().name().toLowerCase())
                                .data(event),
                        cleanup,
                        "事件发送失败");
                if (sent && isTerminal(event)) {
                    emitter.complete();
                }
            });
            subscription.set(registered);
            if (closed.get()) cleanup.run();
            AdminTaskEvent current = taskService.find(taskId).orElse(null);
            if (current != null && isTerminal(current)) {
                cleanup.run();
                emitter.complete();
            }
        } catch (RuntimeException exception) {
            cleanup.run();
            emitter.completeWithError(exception);
        }
        return emitter;
    }

    private void sendKeepAlive(
            SseEmitter emitter,
            UUID taskId,
            Object sendLock,
            Runnable cleanup) {
        send(
                emitter,
                taskId,
                sendLock,
                SseEmitter.event()
                        .name("keepalive")
                        .data("ping"),
                cleanup,
                "保活发送失败");
    }

    /**
     * 所有 SSE 写入共享同一连接异常边界，避免事件和保活各自复制清理逻辑。
     *
     * @return {@code true} 表示事件已写入，{@code false} 表示连接已清理
     */
    private static boolean send(
            SseEmitter emitter,
            UUID taskId,
            Object sendLock,
            SseEmitter.SseEventBuilder event,
            Runnable cleanup,
            String operation) {
        try {
            synchronized (sendLock) {
                emitter.send(event);
            }
            return true;
        } catch (IOException | RuntimeException exception) {
            disconnect(emitter, taskId, cleanup, operation, exception);
            return false;
        }
    }

    private AdminTaskEvent findAuthorized(UUID taskId) {
        AdminTaskEvent event = taskService.find(taskId)
                .orElseThrow(() -> new DataNotFoundException("管理端任务不存在"));
        StpUtil.checkPermission(taskService.requiredPermission(taskId)
                .orElseThrow(() -> new DataNotFoundException("管理端任务不存在")));
        return event;
    }

    private static boolean isTerminal(AdminTaskEvent event) {
        return event.status() == AdminTaskStatus.COMPLETED
                || event.status() == AdminTaskStatus.FAILED;
    }

    private static void disconnect(
            SseEmitter emitter,
            UUID taskId,
            Runnable cleanup,
            String operation,
            Exception exception) {
        log.debug("管理端 SSE {}, taskId={}", operation, taskId, exception);
        cleanup.run();
        emitter.complete();
    }

    /**
     * 连接结束时同时取消保活任务和状态订阅。该方法必须幂等，因为完成、超时、
     * 发送异常可能并发触发。
     */
    private static void close(
            AtomicReference<AdminTaskService.TaskSubscription> subscription,
            AtomicReference<ScheduledFuture<?>> keepAlive) {
        ScheduledFuture<?> keepAliveTask = keepAlive.getAndSet(null);
        if (keepAliveTask != null) {
            keepAliveTask.cancel(false);
        }
        AdminTaskService.TaskSubscription value = subscription.getAndSet(null);
        if (value != null) {
            value.close();
        }
    }
}
