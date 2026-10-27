package com.nnm.bot.core.onebot.message;

import com.nnm.bot.core.config.BotCacheProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.util.UUID;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.springframework.stereotype.Component;

/** 在 Shiro 排队前记录本机接收时间；不依赖跨线程 ThreadLocal 或平台时钟。 */
@Component
public class OneBotMessageArrivalStore {
    @Resource
    private BotCacheProperties properties;
    private Cache<Key, Long> arrivals;

    @PostConstruct
    public void initialize() {
        arrivals = new Cache2kBuilder<Key, Long>() { }
                .name("nnm-onebot-arrivals-" + UUID.randomUUID())
                .entryCapacity(properties.replayCapacity()).strictEviction(true)
                .expireAfterWrite(properties.replayRetention()).build();
    }

    public void record(String sessionId, String accountId, String messageId, long receivedAt) {
        arrivals.putIfAbsent(new Key(sessionId, accountId, messageId), receivedAt);
    }

    public long receivedAt(String sessionId, String accountId, String messageId) {
        Long receivedAt = arrivals.peek(new Key(sessionId, accountId, messageId));
        if (receivedAt == null) {
            throw new IllegalStateException("OneBot arrival timestamp missing or expired");
        }
        return receivedAt;
    }

    @PreDestroy
    public void close() {
        arrivals.close();
    }

    private record Key(String sessionId, String accountId, String messageId) { }
}
