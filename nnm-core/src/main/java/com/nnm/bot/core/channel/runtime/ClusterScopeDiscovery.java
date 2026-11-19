package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.common.error.SystemException;
import com.nnm.bot.core.channel.constant.BotChannelErrorCode;
import java.util.Optional;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.mq.message.discovery.OneBotScopeDiscoveryDTO;
import com.nnm.bot.core.mq.message.discovery.OneBotScopeDiscoveryMessage;
import io.github.rdt.queue.RdtMessageSendManager;
import io.github.rdt.exception.RdtErrorCode;
import io.github.rdt.exception.RdtQueueException;
import com.nnm.bot.core.channel.discovery.store.OneBotScopeDiscoveryResponseStore;
import com.nnm.bot.core.config.ClusterProperties;
import com.nnm.bot.core.channel.registry.ChannelProviderRegistry;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * 处理跨实例的只读群组发现请求。
 *
 * <p>
 * 消息只携带标识和过期时间，不携带机器人凭据，也不传递可执行回调。
 * </p>
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
    private ChannelProviderRegistry providers;

    public BotScopeDiscoveryResult discover(BotChannelInstanceRecord instance) {
        BotChannelKey key = new BotChannelKey(instance.platform(), instance.botAccountId());
        var provider = providers.localScopeDiscovery(instance.platform()).orElse(null);
        if (provider == null)
            return BotScopeDiscoveryResult.unsupported("渠道未注册本地发现服务");
        if (runtime.isLocalOwner(key))
            return provider.discoverLocal(instance.botAccountId());
        if (runtime.snapshot(key).isEmpty())
            return BotScopeDiscoveryResult.unsupported("机器人当前未连接");
        OneBotScopeDiscoveryDTO request = new OneBotScopeDiscoveryDTO(UUID.randomUUID(), instance.platform(),
            instance.botAccountId(), Instant.now().plus(properties.discoveryTimeout()));
        try {
            messages.send(new OneBotScopeDiscoveryMessage(request));
        } catch (RdtQueueException rejected) {
            // 准入拒绝表示没有请求被接受，不能继续等待不存在的响应。
            if (rejected.code() == RdtErrorCode.QUEUE_CAPACITY)
                return BotScopeDiscoveryResult.unsupported("群组发现队列已满，请稍后重试");
            if (rejected.code() == RdtErrorCode.MESSAGE_TOO_LARGE)
                return BotScopeDiscoveryResult.unsupported("群组发现请求超过消息大小限制");
            throw rejected;
        }
        long deadline = System.nanoTime() + properties.discoveryTimeout().toNanos();
        while (System.nanoTime() < deadline) {
            Optional<BotScopeDiscoveryResult> result = responses.find(request.requestId());
            if (result.isPresent())
                return result.get();
            try {
                Thread.sleep(Math.min(250, properties.taskPollInterval().toMillis()));
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new SystemException(BotChannelErrorCode.SCOPE_DISCOVERY_INTERRUPTED,
                    "Group discovery interrupted", exception);
            }
        }
        return BotScopeDiscoveryResult.unsupported("机器人所属节点未在时限内响应，请稍后重试");
    }
}
