package com.nnm.bot.core.group.service;

import com.nnm.bot.core.channel.port.BotChannelInstanceIdentityResolver;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.core.message.model.BotMessageContext;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 统一解析消息所属的逻辑群组。
 * OneBot、QQ 官方机器人和后续渠道都应从这里取得群组边界，避免各命令自行拼接查询条件。
 */
@Component
public class BotGroupCommandContextResolver {

    @Resource
    private BotChannelInstanceIdentityResolver instanceIdentityResolver;
    @Resource
    private BotGroupBotRepository groupBotRepository;
    @Resource
    private BotGroupRepository groupRepository;

    public Optional<BotGroupCommandContext> resolve(BotMessageContext context) {
        if (context == null || !context.isGroupMessage()) {
            return Optional.empty();
        }
        var instanceId = instanceIdentityResolver.resolveId(
                context.platform(), context.botAccountId());
        if (instanceId.isEmpty()) {
            return Optional.empty();
        }
        Optional<BotGroupBot> binding = groupBotRepository
                .findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                        context.platform().name(),
                        "GROUP",
                        context.conversationId(),
                        instanceId.getAsLong())
                .filter(BotGroupBot::isEnabled);
        if (binding.isEmpty()) {
            return Optional.empty();
        }
        return groupRepository.findById(binding.get().getGroupId())
                .filter(BotGroup::isEnabled)
                .map(group -> new BotGroupCommandContext(group, binding.get()));
    }
}
