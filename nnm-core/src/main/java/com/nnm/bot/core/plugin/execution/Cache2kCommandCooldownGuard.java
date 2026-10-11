package com.nnm.bot.core.plugin.execution;

import java.util.Locale;
import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.api.CommandCooldownGuard;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import jakarta.annotation.PreDestroy;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheManager;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * 单 JVM 冷却实现。缓存只保存最近执行时间，不承载业务状态。
 */
@Component
public class Cache2kCommandCooldownGuard implements CommandCooldownGuard, AutoCloseable {

    private final CacheManager cacheManager;
    private final Cache<String, Long> executions;

    public Cache2kCommandCooldownGuard() {
        /*
         * Spring 测试会创建多个应用上下文。为每个 Bean 使用独立 manager，
         * 避免默认 manager 中的同名缓存跨上下文残留。
         */
        cacheManager = CacheManager.getInstance(
                "nnm-command-cooldown-" + UUID.randomUUID());
        executions = Cache2kBuilder
                .of(String.class, Long.class)
                .manager(cacheManager)
                .name("executions")
                .entryCapacity(8192)
                .expireAfterWrite(Duration.ofHours(24))
                .build();
    }

    @Override
    public synchronized boolean tryAcquire(BotMessageContext context,
                                            CommandDescriptor descriptor,
                                            long cooldownSeconds) {
        if (cooldownSeconds <= 0) {
            return true;
        }
        String key = String.join("|",
                context.platform().name(),
                context.botAccountId(),
                context.scope().name(),
                context.conversationId(),
                context.senderId(),
                descriptor.code().toLowerCase(Locale.ROOT));
        long now = System.currentTimeMillis();
        Long previous = executions.peek(key);
        if (ObjectUtil.isNotNull(previous) && now - previous < cooldownSeconds * 1000L) {
            return false;
        }
        executions.put(key, now);
        return true;
    }

    @PreDestroy
    @Override
    public void close() {
        cacheManager.close();
    }
}
