package com.nnm.bot.core.channel.discovery.store;

import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.config.ClusterProperties;
import io.github.rdt.state.SharedStateStore;
import jakarta.annotation.Resource;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

/** Cross-instance discovery responses are shared operational state, never a local read cache. */
@Service
public class OneBotScopeDiscoveryResponseStore {
    @Resource
    private SharedStateStore state;
    @Resource
    private ClusterProperties properties;

    public static String resultKey(UUID requestId) {
        return "onebot-scope-discovery-result:" + requestId;
    }

    public void save(UUID requestId, BotScopeDiscoveryResult result) {
        state.put(resultKey(requestId), result, properties.discoveryTimeout());
    }

    public Optional<BotScopeDiscoveryResult> find(UUID requestId) {
        return state.get(resultKey(requestId), BotScopeDiscoveryResult.class);
    }
}
