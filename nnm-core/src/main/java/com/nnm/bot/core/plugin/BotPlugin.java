package com.nnm.bot.core.plugin;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

public interface BotPlugin {

    boolean match(String message);

    void handle(Bot bot, AnyMessageEvent event, String message);

    String command();

    String description();

    default int order() {
        return 0;
    }
}
