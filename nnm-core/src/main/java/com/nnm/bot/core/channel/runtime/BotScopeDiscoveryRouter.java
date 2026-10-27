package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.model.BotMemberQueryResult;
import com.nnm.bot.core.channel.port.BotGroupMemberQueryProvider;
import com.nnm.bot.core.channel.port.BotScopeDiscoveryProvider;
import com.nnm.bot.core.constant.BotChannelCapability;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

/** Routes scope discovery through the channel capability contract. */
@Component
public class BotScopeDiscoveryRouter {

    @Resource
    private List<BotScopeDiscoveryProvider> providers;
    @Resource
    private List<BotGroupMemberQueryProvider> memberProviders;

    public BotScopeDiscoveryResult discover(BotChannelInstanceRecord instance) {
        if (!instance.platform().capabilities().contains(BotChannelCapability.GROUP_DISCOVERY)) {
            return BotScopeDiscoveryResult.unsupported("渠道未提供群组发现能力");
        }
        return providers.stream()
                .filter(provider -> provider.platform() == instance.platform())
                .findFirst()
                .map(provider -> provider.discover(instance))
                .orElseGet(() -> BotScopeDiscoveryResult.unsupported("渠道未注册群组发现服务"));
    }

    public BotMemberQueryResult queryMembers(
            BotChannelInstanceRecord instance, String scopeKey) {
        return memberProviders.stream()
                .filter(provider -> provider.platform() == instance.platform())
                .findFirst()
                .map(provider -> provider.query(instance, scopeKey))
                .orElseGet(() -> new BotMemberQueryResult(List.of(), false));
    }
}
