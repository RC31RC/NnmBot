package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.message.model.BotMessageContext;
import java.util.Optional;

/** Only adapters supporting unsolicited replies may create a context without a native receipt. */
public interface BotReplyContextProvider {
    BotPlatform platform();

    Optional<BotMessageContext> create(BotChannelInstanceMetadata instance, BotGroupBot binding,
                                      BotMessageContext origin, String senderId);
}
