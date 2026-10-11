package com.nnm.bot.core.channel.messaging.consumer;

import com.nnm.bot.core.channel.messaging.model.OneBotScopeDiscoveryRequest;
import com.nnm.bot.core.channel.messaging.store.OneBotScopeDiscoveryResponseStore;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.onebot.discovery.OneBotScopeDiscoveryProvider;
import io.github.rdt.queue.QueueMode;
import io.github.rdt.queue.RdtMessageConsumer;
import io.github.rdt.spring.annotation.RdtQueueListener;
import jakarta.annotation.Resource;
import java.time.Instant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * 消费 OneBot 群组发现请求，只在当前实例持有目标机器人连接时执行本地查询。
 */
@ConditionalOnProperty(prefix = "nnm.distributed", name = "enabled", havingValue = "true", matchIfMissing = true)
@RdtQueueListener(
        value = "onebot-scope-discovery-v1",
        displayName = "OneBot 群组发现请求",
        mode = QueueMode.RELIABLE_BROADCAST,
        group = "onebot-scope-discovery-owners-v1"
)
public class OneBotScopeDiscoveryMessageConsumer
        implements RdtMessageConsumer<OneBotScopeDiscoveryRequest> {
    @Resource
    private ClusterBotRuntime runtime;
    @Resource
    private OneBotScopeDiscoveryProvider oneBot;
    @Resource
    private OneBotScopeDiscoveryResponseStore responses;

    @Override
    public void consume(OneBotScopeDiscoveryRequest request) {
        if (request == null || request.expiresAt() == null
                || Instant.now().isAfter(request.expiresAt())) {
            return;
        }
        BotChannelKey key = new BotChannelKey(BotPlatform.ONEBOT, request.accountId());
        if (!runtime.isLocalOwner(key)) {
            return;
        }
        BotScopeDiscoveryResult result = oneBot.discoverLocal(request.accountId());
        responses.save(request.requestId(), result);
    }
}
