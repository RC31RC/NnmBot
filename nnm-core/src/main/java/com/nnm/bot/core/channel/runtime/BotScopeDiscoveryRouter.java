package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.model.BotMemberQueryResult;
import com.nnm.bot.core.channel.registry.ChannelProviderRegistry;
import com.nnm.bot.core.channel.constant.BotChannelCapability;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

/** 按渠道能力合同路由作用域发现请求。 */
@Component
public class BotScopeDiscoveryRouter {

    @Resource
    private ChannelProviderRegistry providers;

    public BotScopeDiscoveryResult discover(BotChannelInstanceRecord instance) {
        if (!instance.platform().capabilities().contains(BotChannelCapability.GROUP_DISCOVERY)) {
            return BotScopeDiscoveryResult.unsupported("渠道未提供群组发现能力");
        }
        return providers.scopeDiscovery(instance.platform()).map(provider -> provider.discover(instance))
            .orElseGet(() -> BotScopeDiscoveryResult.unsupported("渠道未注册群组发现服务"));
    }

    public BotMemberQueryResult queryMembers(BotChannelInstanceRecord instance, String scopeKey) {
        return providers.groupMemberQuery(instance.platform()).map(provider -> provider.query(instance, scopeKey))
            .orElseGet(() -> new BotMemberQueryResult(List.of(), false));
    }
}
