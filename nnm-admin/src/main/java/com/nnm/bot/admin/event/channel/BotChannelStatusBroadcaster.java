package com.nnm.bot.admin.event.channel;

import com.nnm.bot.admin.service.bot.channel.BotChannelStatusService;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.registry.BotChannelRegistry.Subscription;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;

/** 每个 JVM 只采集一次共享状态，页面数量不再放大数据库和 Redis 读取量。 */
@Slf4j
@Service
public class BotChannelStatusBroadcaster {
    private final ReentrantLock refreshLock = new ReentrantLock();

    @Resource
    private BotChannelStatusService statusService;
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();
    private volatile List<BotChannelSnapshot> latest;

    public Subscription subscribe(Consumer<BotChannelSnapshot> updates, Consumer<Throwable> errors) {
        refreshLock.lock();
        try {
            // 注册与首屏在同一采集锁内完成，避免新状态先发送、旧首屏随后覆盖。
            List<BotChannelSnapshot> initial = snapshots();
            Listener listener = new Listener(updates, errors);
            listeners.add(listener);
            try {
                initial.forEach(updates);
            } catch (RuntimeException failure) {
                listeners.remove(listener);
                throw failure;
            }
            return () -> listeners.remove(listener);
        } finally {
            refreshLock.unlock();
        }
    }

    public List<BotChannelSnapshot> snapshots() {
        refreshLock.lock();
        try {
            if (latest == null) latest = List.copyOf(statusService.snapshots());
            return latest;
        } finally {
            refreshLock.unlock();
        }
    }

    public void refresh() {
        refreshLock.lock();
        try {
            if (listeners.isEmpty()) {
                latest = null;
                return;
            }
            List<BotChannelSnapshot> collected;
            try {
                collected = List.copyOf(statusService.snapshots());
            } catch (RuntimeException failure) {
                for (Listener listener : listeners) fail(listener, failure);
                return;
            }
            Map<String, BotChannelSnapshot> previous = latest == null ? Map.of() : latest.stream()
                    .collect(Collectors.toMap(BotChannelSnapshot::instanceId, Function.identity()));
            List<BotChannelSnapshot> changes = collected.stream()
                    .filter(snapshot -> !snapshot.equals(previous.get(snapshot.instanceId()))).toList();
            latest = collected;
            for (Listener listener : listeners) {
                try {
                    changes.forEach(listener.updates());
                } catch (RuntimeException failure) {
                    // 一个已断开的页面不能中断本节点其余订阅者的状态更新。
                    fail(listener, failure);
                }
            }
        } finally {
            refreshLock.unlock();
        }
    }

    private void fail(Listener listener, RuntimeException failure) {
        listeners.remove(listener);
        try {
            listener.errors().accept(failure);
        } catch (RuntimeException callbackFailure) {
            log.debug("机器人状态订阅清理失败", callbackFailure);
        }
    }

    private record Listener(Consumer<BotChannelSnapshot> updates, Consumer<Throwable> errors) {
    }
}
