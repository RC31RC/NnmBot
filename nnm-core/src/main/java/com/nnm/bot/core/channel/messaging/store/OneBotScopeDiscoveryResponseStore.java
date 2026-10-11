package com.nnm.bot.core.channel.messaging.store;

import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import io.github.rdt.cache.RdtCachePut;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** 保存跨实例群组发现结果，供发起请求的实例轮询读取。 */
@Service
@ConditionalOnProperty(prefix = "nnm.distributed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OneBotScopeDiscoveryResponseStore {

    public static String resultKey(UUID requestId) {
        return "onebot-scope-discovery-result:" + requestId;
    }

    /** 使用 RDT 缓存注解让结果在超时后自动清理。 */
    @RdtCachePut(
            key = "'onebot-scope-discovery-result:' + #p0",
            ttl = "${nnm.distributed.discovery-timeout:60s}"
    )
    public BotScopeDiscoveryResult save(UUID requestId, BotScopeDiscoveryResult result) {
        return result;
    }
}
