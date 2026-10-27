package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.config.BotCacheProperties;
import jakarta.annotation.PostConstruct;
import com.nnm.bot.core.group.service.BotGroupCommandContext;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.api.CommandCooldownGuard;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheManager;
import org.springframework.stereotype.Component;

/** 单 JVM 冷却实现，身份归一只改变 key，不把冷却扩展为 Redis 状态。 */
@Component
public class Cache2kCommandCooldownGuard implements CommandCooldownGuard, AutoCloseable {

    @Resource
    private BotCacheProperties properties;
    private CacheManager cacheManager;
    private Cache<String, Long> executions;
    @Resource
    private BotGroupCommandContextResolver groupResolver;
    @Resource
    private BotGroupMemberIdentityResolver memberResolver;

    @PostConstruct
    public void initialize() {
        cacheManager = CacheManager.getInstance("nnm-command-cooldown-" + UUID.randomUUID());
        executions = Cache2kBuilder
                .of(String.class, Long.class)
                .manager(cacheManager)
                .name("executions")
                .entryCapacity(properties.cooldownCapacity())
                .expireAfterWrite(properties.cooldownRetention())
                .build();
    }

    @Override
    public boolean tryAcquire(BotMessageContext context,
                                            CommandDescriptor descriptor,
                                            long cooldownSeconds) {
        if (cooldownSeconds <= 0) {
            return true;
        }
        String key = cooldownKey(context, descriptor);
        long now = System.nanoTime();
        long cooldown = Duration.ofSeconds(cooldownSeconds).toNanos();
        // cache2k 按 key 原子处理，互不相关的群组和成员不争用一个 JVM 全局锁。
        return executions.invoke(key, entry -> {
            Long previous = entry.exists() ? entry.getValue() : null;
            if (previous != null && now - previous < cooldown) return false;
            entry.setValue(now);
            return true;
        });
    }

    private String cooldownKey(BotMessageContext context, CommandDescriptor descriptor) {
        Optional<BotGroupCommandContext> group = groupResolver == null ? Optional.<BotGroupCommandContext>empty()
                : groupResolver.resolve(context);
        if (group.isPresent()) {
            Optional<BotGroupMember> member = memberResolver == null ? Optional.<BotGroupMember>empty()
                    : memberResolver.findByContext(group.get().group().getId(), context);
            String identity = member.filter(item -> item.getId() != null)
                    .map(item -> "member:" + item.getId())
                    .orElseGet(() -> "native:" + context.platform().name()
                            + ":" + context.senderId());
            return "group:" + group.get().group().getId() + "|" + identity + "|"
                    + descriptor.code().strip().toLowerCase(Locale.ROOT);
        }
        return String.join("|", "private", context.platform().name(), context.botAccountId(), context.scope().name(),
                context.conversationId(), context.senderId(),
                descriptor.code().strip().toLowerCase(Locale.ROOT));
    }

    @PreDestroy
    @Override
    public void close() {
        if (cacheManager != null) cacheManager.close();
    }
}
