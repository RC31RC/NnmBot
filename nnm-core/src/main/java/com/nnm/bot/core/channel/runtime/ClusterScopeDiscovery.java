package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.messaging.model.OneBotScopeDiscoveryRequest;
import com.nnm.bot.core.channel.messaging.producer.OneBotScopeDiscoveryMessageProducer;
import com.nnm.bot.core.channel.messaging.store.OneBotScopeDiscoveryResponseStore;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.onebot.discovery.OneBotScopeDiscoveryProvider;
import com.nnm.bot.core.config.DistributedProperties;
import io.github.rdt.cache.CacheEntry;
import io.github.rdt.cache.CacheService;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 处理跨实例的只读群组发现请求。
 *
 * <p>消息只携带标识和过期时间，不携带机器人凭据，也不传递可执行回调。</p>
 */
@Service
@ConditionalOnProperty(prefix = "nnm.distributed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ClusterScopeDiscovery {
    @Resource
    private ClusterBotRuntime runtime;
    @Resource
    private OneBotScopeDiscoveryMessageProducer producer;
    @Resource
    private CacheService cache;
    @Resource
    private DistributedProperties properties;
    @Resource
    private ObjectProvider<OneBotScopeDiscoveryProvider> oneBot;

    public BotScopeDiscoveryResult discover(BotChannelInstanceRecord instance) {
        BotChannelKey key = new BotChannelKey(instance.platform(), instance.botAccountId());
        if (runtime.isLocalOwner(key)) return oneBot.getObject().discoverLocal(instance.botAccountId());
        if (runtime.snapshot(key).isEmpty()) return BotScopeDiscoveryResult.unsupported("机器人当前未连接");
        OneBotScopeDiscoveryRequest request = new OneBotScopeDiscoveryRequest(UUID.randomUUID(), instance.botAccountId(),
                Instant.now().plus(properties.discoveryTimeout()));
        producer.publish(request);
        long deadline = System.nanoTime() + properties.discoveryTimeout().toNanos();
        while (System.nanoTime() < deadline) {
            CacheEntry<BotScopeDiscoveryResult> result = cache.get(
                    OneBotScopeDiscoveryResponseStore.resultKey(request.requestId()),
                    BotScopeDiscoveryResult.class);
            if (result instanceof CacheEntry.Hit<BotScopeDiscoveryResult> hit) return hit.value();
            try {
                Thread.sleep(Math.min(250, properties.taskPollInterval().toMillis()));
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Group discovery interrupted", exception);
            }
        }
        return BotScopeDiscoveryResult.unsupported("机器人所属节点未在时限内响应，请稍后重试");
    }
}
