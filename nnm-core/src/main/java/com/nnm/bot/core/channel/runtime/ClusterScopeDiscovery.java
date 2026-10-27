package com.nnm.bot.core.channel.runtime;

import java.util.Optional;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.mq.message.discovery.OneBotScopeDiscoveryRequest;
import com.nnm.bot.core.mq.message.discovery.OneBotScopeDiscoveryMessage;
import io.github.rdt.queue.RdtMessageSendManager;
import io.github.rdt.exception.RdtErrorCode;
import io.github.rdt.exception.RdtQueueException;
import com.nnm.bot.core.channel.discovery.store.OneBotScopeDiscoveryResponseStore;
import com.nnm.bot.core.config.ClusterProperties;
import com.nnm.bot.core.channel.port.BotLocalScopeDiscoveryProvider;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.UUID;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 处理跨实例的只读群组发现请求。
 *
 * <p>消息只携带标识和过期时间，不携带机器人凭据，也不传递可执行回调。</p>
 */
@Service
public class ClusterScopeDiscovery {
    @Resource
    private ClusterBotRuntime runtime;
    @Resource
    private RdtMessageSendManager messages;
    @Resource
    private OneBotScopeDiscoveryResponseStore responses;
    @Resource
    private ClusterProperties properties;
    @Resource
    private List<BotLocalScopeDiscoveryProvider> providers;

    public BotScopeDiscoveryResult discover(BotChannelInstanceRecord instance) {
        BotChannelKey key = new BotChannelKey(instance.platform(), instance.botAccountId());
        BotLocalScopeDiscoveryProvider provider = providers.stream()
                .filter(item -> item.platform() == instance.platform())
                .findFirst().orElse(null);
        if (provider == null) return BotScopeDiscoveryResult.unsupported("渠道未注册本地发现服务");
        if (runtime.isLocalOwner(key)) return provider.discoverLocal(instance.botAccountId());
        if (runtime.snapshot(key).isEmpty()) return BotScopeDiscoveryResult.unsupported("机器人当前未连接");
        OneBotScopeDiscoveryRequest request = new OneBotScopeDiscoveryRequest(
                UUID.randomUUID(), instance.platform(), instance.botAccountId(),
                Instant.now().plus(properties.discoveryTimeout()));
        try {
            messages.send(new OneBotScopeDiscoveryMessage(request));
        } catch (RdtQueueException rejected) {
            // Admission rejection means no request was accepted; do not wait for an impossible response.
            if (rejected.code() == RdtErrorCode.QUEUE_CAPACITY)
                return BotScopeDiscoveryResult.unsupported("群组发现队列已满，请稍后重试");
            if (rejected.code() == RdtErrorCode.MESSAGE_TOO_LARGE)
                return BotScopeDiscoveryResult.unsupported("群组发现请求超过消息大小限制");
            throw rejected;
        }
        long deadline = System.nanoTime() + properties.discoveryTimeout().toNanos();
        while (System.nanoTime() < deadline) {
            Optional<BotScopeDiscoveryResult> result = responses.find(request.requestId());
            if (result.isPresent()) return result.get();
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
