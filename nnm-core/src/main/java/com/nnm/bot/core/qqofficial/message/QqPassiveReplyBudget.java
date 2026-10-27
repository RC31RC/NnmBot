package com.nnm.bot.core.qqofficial.message;

import com.nnm.bot.core.constant.qqofficial.QqReplyConstants.SlotStatus;

import io.github.rdt.lock.LockScope;
import com.nnm.bot.core.constant.QqOfficialChatType;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.qqofficial.config.QqReplyProperties;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.state.SharedStateStore;
import jakarta.annotation.Resource;
import java.time.Clock;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

/** 一条原始消息共用一份账本；短锁只覆盖共享状态，不覆盖渲染、上传或网络发送。 */
@Component
public class QqPassiveReplyBudget {
    @Resource
    private SharedStateStore states;
    @Resource
    private DistributedLockManager locks;
    @Resource
    private QqReplyProperties properties;
    @Resource(name = "qqReplyClock")
    private Clock clock;

    public boolean canReserve(BotMessageContext context, int count) {
        QqOfficialMessageSource source = context.nativeSource(QqOfficialMessageSource.class);
        if (source.messageId() == null) return true;
        if (!valid(context, source, count)) return false;
        if (!limited(source.chatType())) return true;
        String key = key(context, source);
        try (LockScope scope = locks.beginScope()) {
            locks.lock(key);
            Ledger ledger = ledger(key, deadline(context, source));
            return live(ledger.deadline()) && ledger.slots().size() + count <= limit(source.chatType())
                    && ledger.nextSequence() + count <= 65536;
        }
    }

    public List<BotMessageSendPermit> reserve(BotMessageContext context, int count) {
        if (count < 1) throw new IllegalArgumentException("count must be positive");
        QqOfficialMessageSource source = context.nativeSource(QqOfficialMessageSource.class);
        if (source.messageId() == null) {
            return IntStream.range(0, count).mapToObj(index -> BotMessageSendPermit.unrestricted()).toList();
        }
        if (!valid(context, source, count)) throw unavailable();
        if (!limited(source.chatType())) {
            return IntStream.range(0, count)
                    .mapToObj(index -> (BotMessageSendPermit) new WindowPermit(deadline(context, source))).toList();
        }
        String key = key(context, source);
        try (LockScope scope = locks.beginScope()) {
            locks.lock(key);
            Ledger ledger = ledger(key, deadline(context, source));
            if (!live(ledger.deadline()) || ledger.slots().size() + count > limit(source.chatType())
                    || ledger.nextSequence() + count > 65536) throw unavailable();
            Map<String, SlotStatus> slots = new LinkedHashMap<>(ledger.slots());
            List<BotMessageSendPermit> permits = new ArrayList<>(count);
            int sequence = ledger.nextSequence();
            for (int index = 0; index < count; index++) {
                String id = UUID.randomUUID().toString();
                slots.put(id, SlotStatus.RESERVED);
                permits.add(new Permit(key, id, sequence++));
            }
            save(key, new Ledger(ledger.deadline(), sequence, slots));
            return List.copyOf(permits);
        }
    }

    private void beginSend(String key, String id) {
        try (LockScope scope = locks.beginScope()) {
            locks.lock(key);
            Ledger ledger = states.<Ledger>get(key, Ledger.class).orElseThrow(this::unavailable);
            if (!live(ledger.deadline()) || ledger.slots().get(id) != SlotStatus.RESERVED) throw unavailable();
            Map<String, SlotStatus> slots = new LinkedHashMap<>(ledger.slots());
            slots.put(id, SlotStatus.USED);
            // 先持久化使用事实，再调用平台；进程崩溃或结果未知时额度仍被占用。
            save(key, new Ledger(ledger.deadline(), ledger.nextSequence(), slots));
        }
    }

    private void release(String key, String id, boolean afterAttempt) {
        try (LockScope scope = locks.beginScope()) {
            locks.lock(key);
            Ledger ledger = states.<Ledger>get(key, Ledger.class).orElse(null);
            if (ledger == null) return;
            SlotStatus status = ledger.slots().get(id);
            if (status == null || status == SlotStatus.USED && !afterAttempt) return;
            Map<String, SlotStatus> slots = new LinkedHashMap<>(ledger.slots());
            slots.remove(id);
            // 归还额度也不回退序号，避免平台对 msg_id + msg_seq 的去重碰撞。
            save(key, new Ledger(ledger.deadline(), ledger.nextSequence(), slots));
        }
    }

    private boolean valid(BotMessageContext context, QqOfficialMessageSource source, int count) {
        return count > 0 && context.receivedAt() > 0 && live(deadline(context, source))
                && (!limited(source.chatType()) || count <= limit(source.chatType()));
    }

    private long deadline(BotMessageContext context, QqOfficialMessageSource source) {
        Duration window = source.chatType() == QqOfficialChatType.C2C
                ? Duration.ofHours(1) : Duration.ofMinutes(5);
        long createdAt = source.messageCreatedAt() == null ? context.receivedAt()
                : Math.min(context.receivedAt(), source.messageCreatedAt().toEpochMilli());
        return createdAt + window.minus(properties.getSafetyMargin()).toMillis();
    }

    private boolean live(long deadline) {
        return clock.millis() < deadline;
    }

    private Ledger ledger(String key, long deadline) {
        // 重复事件或另一实例的接收时间不同，不能据此重建账本、延长时限或重置次数。
        return states.<Ledger>get(key, Ledger.class)
                .map(existing -> new Ledger(Math.min(existing.deadline(), deadline),
                        existing.nextSequence(), existing.slots()))
                .orElseGet(() -> new Ledger(deadline, 1, Map.of()));
    }

    private void save(String key, Ledger ledger) {
        Duration ttl = Duration.ofMillis(Math.max(1, ledger.deadline() - clock.millis()))
                .plus(properties.getLedgerRetention());
        states.put(key, ledger, ttl);
    }

    private String key(BotMessageContext context, QqOfficialMessageSource source) {
        String identity = context.botAccountId() + '\n' + source.chatType()
                + '\n' + source.targetId() + '\n' + source.messageId();
        try {
            return "qq-passive-reply:" + HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(identity.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private boolean limited(QqOfficialChatType type) {
        return type == QqOfficialChatType.C2C || type == QqOfficialChatType.GROUP;
    }

    private int limit(QqOfficialChatType type) {
        return type == QqOfficialChatType.C2C
                ? properties.getC2cReplyLimit() : properties.getGroupReplyLimit();
    }

    private BotMessageNotSentException unavailable() {
        return new BotMessageNotSentException("QQ reply window or remaining reply quota is insufficient");
    }

    public record Ledger(long deadline, int nextSequence, Map<String, SlotStatus> slots) {
        public Ledger {
            slots = Map.copyOf(slots);
        }
    }

    private final class WindowPermit implements BotMessageSendPermit {
        private final long deadline;

        private WindowPermit(long deadline) { this.deadline = deadline; }

        @Override
        public Integer messageSequence() { return null; }

        @Override
        public void beforeSend() {
            if (!live(deadline)) throw unavailable();
        }

        @Override
        public void rejected() { }

        @Override
        public void close() { }
    }

    private final class Permit implements BotMessageSendPermit {
        private final ReentrantLock permitLock = new ReentrantLock();

        private final String key;
        private final String id;
        private final int sequence;
        private boolean attempted;
        private boolean closed;

        private Permit(String key, String id, int sequence) {
            this.key = key;
            this.id = id;
            this.sequence = sequence;
        }

        @Override
        public Integer messageSequence() { return sequence; }

        @Override
        public void beforeSend() {
            permitLock.lock();
            try {
                if (attempted || closed) throw unavailable();
                // Redis 写入确认后才允许提交请求；不确定写入的 USED 槽由 close 保守保留。
                beginSend(key, id);
                attempted = true;
            } finally {
                permitLock.unlock();
            }
        }

        @Override
        public void rejected() {
            permitLock.lock();
            try {
                if (closed) return;
                closed = true;
                release(key, id, attempted);
            } finally {
                permitLock.unlock();
            }
        }

        @Override
        public void close() {
            permitLock.lock();
            try {
                if (!attempted) rejected();
            } finally {
                permitLock.unlock();
            }
        }
    }

}
