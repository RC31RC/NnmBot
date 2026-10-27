package com.nnm.bot.core.mq.consumer.discovery;

import com.nnm.bot.core.mq.message.discovery.OneBotScopeDiscoveryMessage;

import com.nnm.bot.core.mq.message.discovery.OneBotScopeDiscoveryRequest;
import com.nnm.bot.core.channel.discovery.store.OneBotScopeDiscoveryResponseStore;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.channel.port.BotLocalScopeDiscoveryProvider;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.mq.messagedescriptor.discovery.OneBotScopeDiscoveryMessageDescriptor;
import com.nnm.bot.core.mq.consumerdescriptor.discovery.OneBotScopeDiscoveryConsumerDescriptor;
import io.github.rdt.queue.RdtMessageConsumer;
import io.github.rdt.spring.annotation.RdtQueueListener;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.List;

/**
 * 消费 OneBot 群组发现请求，只在当前实例持有目标机器人连接时执行本地查询。
 */
@RdtQueueListener(topic = OneBotScopeDiscoveryMessageDescriptor.TOPIC, group = OneBotScopeDiscoveryConsumerDescriptor.GROUP)
public class OneBotScopeDiscoveryMessageConsumer
        implements RdtMessageConsumer<OneBotScopeDiscoveryMessage> {
    @Resource
    private ClusterBotRuntime runtime;
    @Resource
    private List<BotLocalScopeDiscoveryProvider> providers;
    @Resource
    private OneBotScopeDiscoveryResponseStore responses;

    @Override
    public void consume(OneBotScopeDiscoveryMessage message) {
        OneBotScopeDiscoveryRequest request = message.payload();
        if (request == null || request.expiresAt() == null
                || Instant.now().isAfter(request.expiresAt())) {
            return;
        }
        BotPlatform platform = request.platform() == null ? BotPlatform.ONEBOT : request.platform();
        BotChannelKey key = new BotChannelKey(platform, request.accountId());
        if (!runtime.isLocalOwner(key)) {
            return;
        }
        BotLocalScopeDiscoveryProvider provider = providers.stream()
                .filter(item -> item.platform() == platform)
                .findFirst().orElse(null);
        if (provider == null) return;
        BotScopeDiscoveryResult result = provider.discoverLocal(request.accountId());
        responses.save(request.requestId(), result);
    }
}
