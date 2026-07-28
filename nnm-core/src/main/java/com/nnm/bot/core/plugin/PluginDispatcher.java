package com.nnm.bot.core.plugin;

import com.nnm.bot.core.message.BotMessageContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class PluginDispatcher {

    private final List<BotPlugin> plugins;

    public PluginDispatcher(List<BotPlugin> plugins) {
        Objects.requireNonNull(plugins, "plugins must not be null");
        validateUniqueCommands(plugins);
        this.plugins = plugins.stream()
                .sorted(Comparator.comparingInt(BotPlugin::order)
                        .thenComparing(plugin -> normalizeCommand(plugin.command())))
                .toList();
        log.info("Loaded {} bot plugins: {}", this.plugins.size(),
                this.plugins.stream().map(BotPlugin::command).toList());
    }

    public boolean dispatch(BotMessageContext context) {
        Objects.requireNonNull(context, "context must not be null");
        for (BotPlugin plugin : plugins) {
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
            try {
                plugin.handle(context);
            } catch (Exception e) {
                log.error("Plugin [{}] handling failed", plugin.command(), e);
            }
            return true;
        }
        return false;
    }

    public List<BotPlugin> plugins() {
        return plugins;
    }

    private static void validateUniqueCommands(List<BotPlugin> plugins) {
        Map<String, BotPlugin> registry = new HashMap<>();
        for (BotPlugin plugin : plugins) {
            Objects.requireNonNull(plugin, "plugin must not be null");
            String command = normalizeCommand(plugin.command());
            if (command.isEmpty()) {
                throw new IllegalStateException(
                        "Bot plugin command must not be blank: " + plugin.getClass().getName());
            }
            BotPlugin previous = registry.put(command, plugin);
            if (previous != null) {
                throw new IllegalStateException(
                        "Duplicate bot plugin command [%s]: %s / %s".formatted(
                                plugin.command(),
                                previous.getClass().getName(),
                                plugin.getClass().getName()));
            }
        }
    }

    private static String normalizeCommand(String command) {
        return command == null ? "" : command.trim().toLowerCase(Locale.ROOT);
    }
}
