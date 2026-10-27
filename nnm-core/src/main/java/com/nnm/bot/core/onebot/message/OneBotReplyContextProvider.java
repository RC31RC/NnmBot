package com.nnm.bot.core.onebot.message;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.channel.port.BotReplyContextProvider;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.message.model.BotMessageContext;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class OneBotReplyContextProvider implements BotReplyContextProvider {
    @Resource
    private BotContainer bots;

    @Override
    public BotPlatform platform() {
        return BotPlatform.ONEBOT;
    }

    @Override
    public Optional<BotMessageContext> create(BotChannelInstanceMetadata instance, BotGroupBot binding,
                                             BotMessageContext origin, String senderId) {
        Bot bot = bots.robots.get(Long.parseLong(instance.botAccountId()));
        if (bot == null || !"GROUP".equals(binding.getScopeType())) return Optional.empty();
        AnyMessageEvent event = new AnyMessageEvent();
        event.setMessageType("group");
        event.setSelfId(Long.parseLong(instance.botAccountId()));
        event.setGroupId(Long.parseLong(binding.getScopeKey()));
        // No user identity is fabricated for mentions; the adapter can send without one.
        return Optional.of(BotMessageContext.of(BotPlatform.ONEBOT, BotMessageScope.GROUP,
                instance.botAccountId(), senderId == null ? "unknown" : senderId,
                binding.getScopeKey(), null, origin.rawMessage(), origin.message(),
                new OneBotMessageSource(bot, event)).withDispatchId(origin.dispatchId()));
    }
}
