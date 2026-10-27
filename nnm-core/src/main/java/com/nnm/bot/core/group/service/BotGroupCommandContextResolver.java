package com.nnm.bot.core.group.service;

import com.nnm.bot.core.channel.topology.BotTopologySnapshot.Binding;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.topology.BotTopologyStore;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot.Scope;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot;
import com.nnm.bot.core.plugin.model.CommandExecutionContext;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.message.model.BotMessageContext;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 统一解析消息所属的逻辑群组。
 * OneBot、QQ 官方机器人和后续渠道都应从这里取得群组边界，避免各命令自行拼接查询条件。
 */
@Component
public class BotGroupCommandContextResolver {

    @Resource
    private BotTopologyStore topology;

    public Optional<BotGroupCommandContext> resolve(BotMessageContext context) {
        if (context == null || !context.isGroupMessage()) {
            return Optional.empty();
        }
        if (context.execution() != null) return Optional.ofNullable(context.execution().group());
        return resolve(context, topology.snapshot());
    }

    private Optional<BotGroupCommandContext> resolve(BotMessageContext context, BotTopologySnapshot snapshot) {
        Long instanceId = snapshot.instanceIds().get(new BotChannelKey(context.platform(), context.botAccountId()));
        if (instanceId == null) return Optional.empty();
        Binding binding = snapshot.scopes().get(new Scope(instanceId, context.platform().name(), "GROUP", context.conversationId()));
        if (binding == null || !binding.enabled() || !snapshot.enabled(binding.groupId())) return Optional.empty();
        return Optional.of(new BotGroupCommandContext(snapshot.groups().get(binding.groupId()).entity(), binding.entity()));
    }
    public CommandExecutionContext executionView(BotMessageContext context) {
        BotTopologySnapshot snapshot = topology.snapshot();
        BotGroupCommandContext group = context.isGroupMessage() ? resolve(context, snapshot).orElse(null) : null;
        if (group == null) return new CommandExecutionContext(null, null, List.of(), null);
        return new CommandExecutionContext(BotTopologySnapshot.Group.of(group.group()),
                BotTopologySnapshot.Binding.of(group.binding()), snapshot.bindings().getOrDefault(group.group().getId(), List.of())
                .stream().filter(binding -> binding.enabled() && "GROUP".equals(binding.scopeType())).toList(), null);
    }

    /**
     * 返回当前逻辑群组启用的群绑定，供入站关联和身份模式判断复用同一边界。
     */
    public List<BotGroupBot> enabledBindings(long groupId) {
        return topology.snapshot().groupBindings(groupId).stream()
                .filter(BotGroupBot::isEnabled)
                .filter(binding -> "GROUP".equals(binding.getScopeType()))
                .toList();
    }

}
