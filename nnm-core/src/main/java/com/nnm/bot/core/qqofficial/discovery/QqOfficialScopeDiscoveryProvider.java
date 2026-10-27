package com.nnm.bot.core.qqofficial.discovery;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.channel.model.BotDiscoveredScope;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.model.BotMemberQueryResult;
import com.nnm.bot.core.channel.port.BotGroupMemberQueryProvider;
import com.nnm.bot.core.channel.port.BotScopeDiscoveryProvider;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.qqofficial.gateway.QqOfficialGatewayClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * QQ 官方机器人通过 Gateway 事件观察群组，不伪造平台全量群组列表。
 */
@Component
@Slf4j
public class QqOfficialScopeDiscoveryProvider
        implements BotScopeDiscoveryProvider, BotGroupMemberQueryProvider {

    @Resource
    private QqOfficialObservedScopeStore observedScopes;
    @Resource
    private QqOfficialGatewayClient gatewayClient;

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public BotScopeDiscoveryResult discover(BotChannelInstanceRecord instance) {
        List<BotDiscoveredScope> scopes = observedScopes.findActive(instance.botAccountId()).stream()
                .map(scope -> enrichMembers(instance, scope))
                .toList();
        String message = scopes.isEmpty()
                ? "暂未观察到群组。QQ 官方机器人需要先入群并连接 Gateway，收到入群事件或群消息后才能获得 group_openid"
                : "群组来自 QQ 官方 Gateway 观察结果；成员列表通过群管理接口补全（若账号具备权限），不会自动创建绑定";
        return new BotScopeDiscoveryResult(true, message, scopes);
    }

    @Override
    public BotMemberQueryResult query(
            BotChannelInstanceRecord instance, String scopeKey) {
        try {
            return new BotMemberQueryResult(
                    gatewayClient.apiClient(instance.botAccountId()).listGroupMembers(scopeKey), true);
        } catch (RuntimeException exception) {
            log.debug("QQ 官方群成员列表不可用: groupOpenid={}", scopeKey, exception);
            return new BotMemberQueryResult(List.of(), false);
        }
    }

    private BotDiscoveredScope enrichMembers(
            BotChannelInstanceRecord instance, BotDiscoveredScope scope) {
        try {
            BotMemberQueryResult query = query(instance, scope.scopeKey());
            List<BotDiscoveredMember> members = query.members();
            if (!query.complete()) return scope;
            return new BotDiscoveredScope(
                    scope.scopeType(), scope.scopeKey(), scope.displayName(),
                    members.size(), members, true);
        } catch (RuntimeException exception) {
            log.debug("QQ 官方群成员列表不可用，保留 Gateway 增量观察结果: groupOpenid={}",
                    scope.scopeKey(), exception);
            return scope;
        }
    }
}
