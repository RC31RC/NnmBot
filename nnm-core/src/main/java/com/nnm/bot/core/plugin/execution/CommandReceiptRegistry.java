package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.config.BotCacheProperties;
import com.nnm.bot.core.config.CommandRoutingProperties;
import com.nnm.bot.core.message.model.BotMessageContext;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheManager;
import org.springframework.stereotype.Component;

/** 同一连接分组的窗口、原生重放和执行认领全部保留在当前 JVM，不经过 Redis。 */
@Component
public class CommandReceiptRegistry implements AutoCloseable {
    private final ReentrantLock receiptsLock = new ReentrantLock();

    @Resource
    private CommandRoutingProperties properties;
    @Resource
    private BotCacheProperties cacheProperties;
    private CacheManager manager;
    private Cache<String, List<String>> windows;
    private Cache<String, Batch> batches;
    private Cache<String, Boolean> nativeClaims;
    private Cache<String, Boolean> roundClaims;

    @PostConstruct
    public void initialize() {
        manager = CacheManager.getInstance("nnm-receipts-" + UUID.randomUUID());
        windows = new Cache2kBuilder<String, List<String>>() { }.manager(manager).name("windows")
                .entryCapacity(properties.maxPendingCommands()).strictEviction(true)
                .expireAfterWrite(properties.receiptRetention()).sharpExpiry(true).build();
        batches = new Cache2kBuilder<String, Batch>() {}.manager(manager).name("receipts")
                .entryCapacity(properties.maxPendingCommands()).strictEviction(true)
                .expireAfterWrite(properties.receiptRetention()).sharpExpiry(true).build();
        nativeClaims = Cache2kBuilder.of(String.class, Boolean.class).manager(manager).name("native-claims")
                .entryCapacity(cacheProperties.replayCapacity()).strictEviction(true)
                .expireAfterWrite(cacheProperties.replayRetention()).sharpExpiry(true).build();
        roundClaims = Cache2kBuilder.of(String.class, Boolean.class).manager(manager).name("round-claims")
                .entryCapacity(properties.maxPendingCommands()).strictEviction(true)
                .expireAfterWrite(properties.receiptRetention()).sharpExpiry(true).build();
    }

    public String observeLocal(String fingerprint, BotMessageContext context) {
        receiptsLock.lock();
        try {
            List<String> candidates = windows.peek(fingerprint);
            List<String> live = candidates == null ? new ArrayList<>()
                    : new ArrayList<>(candidates.stream().filter(batches::containsKey).toList());
            String selected = null;
            long nearest = Long.MAX_VALUE;
            for (String candidate : live) {
                Batch batch = batches.peek(candidate);
                if (batch == null) continue;
                if (batch.receipts.stream().anyMatch(receipt -> sameReceipt(receipt, context))) {
                    return candidate;
                }
                // 同账号两个不同原生 ID 是真实的两次发送，不能仅因文本相同而合并。
                if (batch.receipts.stream().anyMatch(receipt -> sameAccount(receipt, context))) continue;
                long earliest = Math.min(batch.firstReceivedAt, context.receivedAt());
                long latest = Math.max(batch.lastReceivedAt, context.receivedAt());
                long distance = Math.abs(batch.firstReceivedAt - context.receivedAt());
                if (latest - earliest <= properties.correlationWindow().toMillis() && distance < nearest) {
                    selected = candidate;
                    nearest = distance;
                }
            }
            if (selected == null) {
                if (live.size() >= 64) throw new IllegalStateException("同一命令指纹的在途轮次超过容量");
                selected = UUID.randomUUID().toString();
                live.add(selected);
            }
            register(selected, context);
            windows.put(fingerprint, List.copyOf(live));
            return selected;
        } finally {
            receiptsLock.unlock();
        }
    }

    public void register(String id, BotMessageContext context) {
        receiptsLock.lock();
        try {
            Batch batch = batches.peek(id);
            if (batch == null) {
                batch = new Batch();
                batches.put(id, batch);
            }
            if (batch.receipts.stream().noneMatch(receipt -> sameReceipt(receipt, context))) {
                if (batch.receipts.size() >= 64) throw new IllegalStateException("同一命令轮次的回执超过容量");
                batch.receipts.add(context);
                batch.firstReceivedAt = Math.min(batch.firstReceivedAt, context.receivedAt());
                batch.lastReceivedAt = Math.max(batch.lastReceivedAt, context.receivedAt());
            }
        } finally {
            receiptsLock.unlock();
        }
    }

    public List<BotMessageContext> receipts(String id) {
        receiptsLock.lock();
        try {
            Batch batch = batches.peek(id);
            return batch == null ? List.of() : List.copyOf(batch.receipts);
        } finally {
            receiptsLock.unlock();
        }
    }

    public boolean hasEarlierReceiptFromPlatform(String id, BotMessageContext context) {
        receiptsLock.lock();
        try {
            Batch batch = batches.peek(id);
            if (batch == null) return false;
            for (int index = 0; index < batch.receipts.size(); index++) {
                if (sameReceipt(batch.receipts.get(index), context)) {
                    return batch.receipts.subList(0, index).stream().anyMatch(item -> item.platform() == context.platform());
                }
            }
            return false;
        } finally {
            receiptsLock.unlock();
        }
    }

    public boolean isReplay(BotMessageContext context) {
        String nativeId = GroupCommandCoordinator.nativeIdentity(context);
        return nativeId != null && nativeClaims.containsKey(nativeId);
    }

    public boolean claimLocal(BotMessageContext context) {
        receiptsLock.lock();
        try {
            String nativeId = GroupCommandCoordinator.nativeIdentity(context);
            // 标记后到回执的原生 ID，窗口结束后再次投递也不能重开一轮；失败后不撤销已认领记录。
            if (nativeId != null && !nativeClaims.putIfAbsent(nativeId, true)) return false;
            return context.dispatchId() == null || roundClaims.putIfAbsent(context.dispatchId(), true);
        } finally {
            receiptsLock.unlock();
        }
    }

    private static boolean sameReceipt(BotMessageContext left, BotMessageContext right) {
        return sameAccount(left, right)
                && Objects.equals(left.messageId(), right.messageId());
    }

    private static boolean sameAccount(BotMessageContext left, BotMessageContext right) {
        return left.platform() == right.platform() && left.botAccountId().equals(right.botAccountId());
    }

    @PreDestroy
    @Override
    public void close() {
        if (manager != null) manager.close();
    }

    private static final class Batch {
        private final List<BotMessageContext> receipts = new ArrayList<>();
        private long firstReceivedAt = Long.MAX_VALUE;
        private long lastReceivedAt;
    }
}
