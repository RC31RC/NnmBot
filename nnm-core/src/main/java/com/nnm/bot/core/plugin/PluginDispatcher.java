package com.nnm.bot.core.plugin;

import com.nnm.bot.core.message.BotMessageContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class PluginDispatcher {

    private final CommandRegistry commandRegistry;

    public PluginDispatcher(CommandRegistry commandRegistry) {
        this.commandRegistry = Objects.requireNonNull(commandRegistry,
                "commandRegistry must not be null");
    }

    public boolean dispatch(BotMessageContext context) {
        Objects.requireNonNull(context, "context must not be null");
        BotPlugin exactPlugin = commandRegistry.exact(context.message());
        if (exactPlugin != null) {
            return handle(exactPlugin, context);
        }

        for (BotPlugin plugin : commandRegistry.fallbackPlugins()) {
            boolean matched;
            try {
                matched = plugin.match(context.message());
            } catch (Exception e) {
                log.error("Plugin [{}] matching failed", plugin.command(), e);
                continue;
            }
            if (!matched) {
                continue;
            }
            return handle(plugin, context);
        }
        return false;
    }

    public List<BotPlugin> plugins() {
        return commandRegistry.plugins();
    }

    private boolean handle(BotPlugin plugin, BotMessageContext context) {
        try {
            plugin.handle(context);
        } catch (Exception e) {
            log.error("Plugin [{}] handling failed", plugin.command(), e);
        }
        return true;
    }
}
