package com.nnm.bot.core.plugin;

import com.nnm.bot.core.message.BotMessageContext;

public interface BotPlugin {

    boolean match(String message);

    void handle(BotMessageContext context);

    String command();

    String description();

    default int order() {
        return 0;
    }

    default CommandDescriptor descriptor() {
        return new CommandDescriptor(
                command(),
                command(),
                java.util.Set.of(),
                description(),
                "",
                MatchType.CUSTOM,
                order()
        );
    }
}
