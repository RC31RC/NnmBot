package com.nnm.bot.core.onebot;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.plugin.PluginDispatcher;
import org.springframework.stereotype.Component;

@Shiro
@Component
public class OneBotMessageHandler {

    private final PluginDispatcher pluginDispatcher;

    public OneBotMessageHandler(PluginDispatcher pluginDispatcher) {
        this.pluginDispatcher = pluginDispatcher;
    }

    @AnyMessageHandler
    public boolean onMessage(Bot bot, AnyMessageEvent event) {
        return pluginDispatcher.dispatch(BotMessageContext.from(bot, event));
    }
}
