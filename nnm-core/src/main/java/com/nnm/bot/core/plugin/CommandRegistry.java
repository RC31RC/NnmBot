package com.nnm.bot.core.plugin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Startup-built command index.
 *
 * <p>Exact commands use the map for near O(1) lookup. Plugins with custom
 * matching rules remain in a stable, ordered fallback list.</p>
 */
@Slf4j
@Component
public class CommandRegistry {

    private final List<RegisteredCommand> commands;
    private final Map<String, RegisteredCommand> exactCommands;
    private final List<RegisteredCommand> fallbackCommands;

    public CommandRegistry(List<BotPlugin> plugins) {
        Objects.requireNonNull(plugins, "plugins must not be null");

        this.commands = plugins.stream()
                .map(plugin -> new RegisteredCommand(
                        Objects.requireNonNull(plugin, "plugin must not be null"),
                        plugin.descriptor()))
                .sorted(Comparator.comparingInt((RegisteredCommand command) ->
                                command.descriptor().order())
                        .thenComparing(command ->
                                normalize(command.descriptor().code())))
                .toList();

        Map<String, RegisteredCommand> registeredNames = new HashMap<>();
        Map<String, RegisteredCommand> registeredCodes = new HashMap<>();
        Map<String, RegisteredCommand> registeredPluginCommands = new HashMap<>();
        Map<String, RegisteredCommand> exactIndex = new HashMap<>();
        for (RegisteredCommand command : commands) {
            validateAndRegister(command, registeredNames, registeredCodes,
                    registeredPluginCommands, exactIndex);
        }
        this.exactCommands = Map.copyOf(exactIndex);
        this.fallbackCommands = commands.stream()
                .filter(command -> command.descriptor().matchType() != MatchType.EXACT)
                .toList();

        log.info("Registered {} bot commands: {}", commands.size(),
                commands.stream().map(command -> command.descriptor().name()).toList());
    }

    public BotPlugin exact(String message) {
        if (message == null) {
            return null;
        }
        RegisteredCommand command = exactCommands.get(normalize(message));
        return command == null ? null : command.plugin();
    }

    public List<BotPlugin> plugins() {
        return commands.stream().map(RegisteredCommand::plugin).toList();
    }

    public List<BotPlugin> fallbackPlugins() {
        return fallbackCommands.stream().map(RegisteredCommand::plugin).toList();
    }

    public List<CommandDescriptor> descriptors() {
        return commands.stream().map(RegisteredCommand::descriptor).toList();
    }

    public int size() {
        return commands.size();
    }

    private static void validateAndRegister(RegisteredCommand command,
            Map<String, RegisteredCommand> registeredNames,
            Map<String, RegisteredCommand> registeredCodes,
            Map<String, RegisteredCommand> registeredPluginCommands,
            Map<String, RegisteredCommand> exactIndex) {
        CommandDescriptor descriptor = command.descriptor();
        registerUnique(descriptor.code(), command, registeredCodes, "command code");
        registerUnique(command.plugin().command(), command, registeredPluginCommands,
                "plugin command");
        for (String lookupName : descriptor.lookupNames()) {
            String normalized = normalize(lookupName);
            registerUnique(lookupName, command, registeredNames, "lookup name");
            if (descriptor.matchType() == MatchType.EXACT) {
                exactIndex.put(normalized, command);
            }
        }
    }

    private static void registerUnique(String value,
            RegisteredCommand command,
            Map<String, RegisteredCommand> registered,
            String valueType) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Bot plugin " + valueType + " must not be blank: "
                            + command.plugin().getClass().getName());
        }
        String normalized = normalize(value);
        RegisteredCommand previous = registered.put(normalized, command);
        if (previous != null && previous != command) {
            throw new IllegalStateException(
                    "Duplicate bot command [%s] (%s): %s / %s".formatted(
                            value,
                            valueType,
                            previous.plugin().getClass().getName(),
                            command.plugin().getClass().getName()));
        }
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private record RegisteredCommand(BotPlugin plugin, CommandDescriptor descriptor) {

        private RegisteredCommand {
            Objects.requireNonNull(plugin, "plugin must not be null");
            Objects.requireNonNull(descriptor, "plugin descriptor must not be null: "
                    + plugin.getClass().getName());
        }
    }
}
