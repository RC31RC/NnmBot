package com.nnm.bot.core.plugin.registry;

import java.util.Map.Entry;
import java.lang.Character.UnicodeScript;
import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.core.constant.CommandBoundaryPolicy;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.api.CommandConfigurationProvider;
import com.nnm.bot.core.plugin.model.CommandDefinition;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.model.CommandTrigger;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 将静态插件实现和数据库命令定义合并为运行时索引。
 */
@Slf4j
@Component
public class CommandRegistry {
    private final ReentrantLock refreshLock = new ReentrantLock();

    @Resource
    private List<BotPlugin> botPlugins;
    @Resource
    private ObjectProvider<CommandConfigurationProvider> configurationProvider;

    /**
     * 匹配线程始终读取完整的不可变快照，刷新失败或刷新进行中都不会暴露半成品索引。
     */
    private volatile Snapshot snapshot = Snapshot.empty();

    @PostConstruct
    void initialize() {
        this.refresh();
    }

    /**
     * 完整构建成功后再替换快照，配置错误时保留当前可用索引。
     */
    public void refresh() {
        refreshLock.lock();
        try {
            Snapshot replacement = buildSnapshot();
            snapshot = replacement;
            log.info("Registered {} bot commands: {}",
                    replacement.commands().size(),
                    replacement.commands().stream()
                            .map(command -> command.descriptor().code())
                            .toList());
        } finally {
            refreshLock.unlock();
        }
    }

    /**
     * 管理端写事务提交后刷新，确保快照读取到已提交数据。
     */
    public void refreshAfterCommit() {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            refreshSafely();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        refreshSafely();
                    }
                });
    }

    /**
     * 先执行完整文本的精确索引查找，再按别名长度匹配前缀命令。
     *
     * <p>参数边界由每个触发词的 {@link CommandBoundaryPolicy} 决定，因此英文命令
     * 不会误匹配普通单词，允许中文紧邻参数的命令也无需在插件中重复切字符串。</p>
     */
    public Optional<CommandMatch> match(BotMessageContext context) {
        Objects.requireNonNull(context, "context must not be null");
        String message = normalize(context.message());
        if (message.isEmpty()) {
            return Optional.empty();
        }

        Snapshot current = snapshot;
        Lookup exact = current.exactCommands().get(message);
        if (ObjectUtil.isNotNull(exact)) {
            return Optional.of(toMatch(context, exact, ""));
        }
        for (Lookup lookup : current.prefixCommands()) {
            String arguments = prefixArguments(
                    message, lookup.normalizedAlias(), lookup.boundaryPolicy());
            if (ObjectUtil.isNotNull(arguments)) {
                return Optional.of(toMatch(context, lookup, arguments));
            }
        }
        return Optional.empty();
    }

    public List<RuntimeCommand> commands() {
        return snapshot.commands();
    }

    public List<BotPlugin> plugins() {
        return snapshot.commands().stream().map(RuntimeCommand::plugin).toList();
    }

    /**
     * 返回所有静态插件实现，管理端可用它检查数据库命令配置是否完整。
     */
    public List<BotPlugin> allPlugins() {
        return botPlugins.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(
                                (BotPlugin plugin) -> metadata(plugin).order())
                        .thenComparing(plugin -> normalize(metadata(plugin).code())))
                .toList();
    }

    public Optional<BotPlugin> plugin(String code) {
        if (ObjectUtil.isNull(code)) {
            return Optional.empty();
        }
        String normalized = normalize(code);
        return allPlugins().stream()
                .filter(plugin -> normalize(metadata(plugin).code()).equals(normalized))
                .findFirst();
    }

    /**
     * 返回插件声明的稳定元数据，供管理端展示和运行时注册使用。
     */
    public BotPluginCommand metadata(BotPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin must not be null");
        BotPluginCommand metadata = AnnotationUtils.findAnnotation(
                plugin.getClass(), BotPluginCommand.class);
        if (ObjectUtil.isNull(metadata)) {
            throw new IllegalStateException(
                    "BotPlugin must be annotated with @BotPluginCommand: "
                            + plugin.getClass().getName());
        }
        return metadata;
    }

    public List<CommandDescriptor> descriptors() {
        return snapshot.commands().stream().map(RuntimeCommand::descriptor).toList();
    }

    public Optional<RuntimeCommand> command(String code) {
        if (ObjectUtil.isNull(code)) {
            return Optional.empty();
        }
        return Optional.ofNullable(snapshot.commandsByCode().get(normalize(code)));
    }

    public int size() {
        return snapshot.commands().size();
    }

    private Snapshot buildSnapshot() {
        Objects.requireNonNull(this.botPlugins, "botPlugins must not be null");
        Map<String, BotPlugin> pluginsByCode = new LinkedHashMap<>();
        this.botPlugins.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(
                                (BotPlugin plugin) -> metadata(plugin).order())
                        .thenComparing(plugin -> normalize(metadata(plugin).code())))
                .forEach(plugin -> registerPlugin(pluginsByCode, plugin));
        log.info("Discovered {} BotPlugin implementations: {}",
                pluginsByCode.size(), pluginsByCode.keySet());

        CommandConfigurationProvider provider = this.configurationProvider.getIfAvailable();
        Map<String, CommandDefinition> definitions = ObjectUtil.isNull(provider)
                ? Map.of()
                : Objects.requireNonNullElse(provider.loadAll(), Map.of());
        Map<String, CommandDefinition> definitionsByCode = new HashMap<>();
        definitions.forEach((key, definition) -> {
            if (ObjectUtil.isNotNull(definition)) {
                CommandDefinition previous = definitionsByCode.put(
                        normalize(definition.code()), definition);
                if (ObjectUtil.isNotNull(previous)) {
                    throw new IllegalStateException(
                            "Duplicate database command code: " + definition.code());
                }
            }
        });
        log.info("Loaded {} database command definitions", definitionsByCode.size());

        List<RuntimeCommand> commands = new ArrayList<>();
        Map<String, RuntimeCommand> commandsByCode = new LinkedHashMap<>();
        Map<String, Lookup> allLookupNames = new HashMap<>();
        Map<String, Lookup> exactCommands = new HashMap<>();
        List<Lookup> prefixCommands = new ArrayList<>();

        for (Entry<String,BotPlugin> entry : pluginsByCode.entrySet()) {
            BotPlugin plugin = entry.getValue();
            CommandDefinition definition = definitionsByCode.remove(entry.getKey());
            if (ObjectUtil.isNull(definition)) {
                // 插件是稳定实现，数据库只负责覆盖可变配置；未配置时使用安全默认值。
                definition = defaultDefinition(plugin);
                log.debug("Bot plugin [{}] has no database command definition; using defaults",
                        metadata(plugin).code());
            }
            CommandExecutionConfiguration configuration = definition.configuration();
            CommandDescriptor descriptor = new CommandDescriptor(
                    definition.code(),
                    definition.name(),
                    configuration.triggers(),
                    definition.description(),
                    configuration.permissionOverride(),
                    metadata(plugin).matchType(),
                    metadata(plugin).order());
            RuntimeCommand runtimeCommand =
                    new RuntimeCommand(plugin, descriptor, configuration);
            commands.add(runtimeCommand);
            commandsByCode.put(entry.getKey(), runtimeCommand);
            for (CommandTrigger trigger : descriptor.lookupTriggers()) {
                Lookup lookup = new Lookup(
                        runtimeCommand,
                        trigger.name(),
                        normalize(trigger.name()),
                        trigger.boundaryPolicy());
                registerLookup(allLookupNames, lookup);
                if (descriptor.matchType() == MatchType.EXACT) {
                    exactCommands.put(lookup.normalizedAlias(), lookup);
                } else {
                    prefixCommands.add(lookup);
                }
            }
        }

        definitionsByCode.values().forEach(definition ->
                log.warn("Database command [{}] has no BotPluginCommand implementation",
                        definition.code()));
        prefixCommands.sort(Comparator
                .comparingInt((Lookup lookup) -> lookup.normalizedAlias().length())
                .reversed()
                .thenComparingInt(lookup -> lookup.command().descriptor().order())
                .thenComparing(Lookup::normalizedAlias));

        return new Snapshot(
                List.copyOf(commands),
                Map.copyOf(commandsByCode),
                Map.copyOf(exactCommands),
                List.copyOf(prefixCommands));
    }

    private CommandDefinition defaultDefinition(BotPlugin plugin) {
        BotPluginCommand metadata = metadata(plugin);
        String code = requireText(metadata.code(), "plugin code");
        String name = requireText(metadata.name(), "plugin name");
        String gameCode = requireText(metadata.gameCode(), "plugin gameCode");
        return new CommandDefinition(
                code,
                name,
                gameCode,
                "",
                CommandExecutionConfiguration.disabledDefaults());
    }

    private void registerPlugin(Map<String, BotPlugin> plugins, BotPlugin plugin) {
        BotPluginCommand metadata = metadata(plugin);
        String code = requireText(metadata.code(), "plugin code");
        BotPlugin previous = plugins.put(normalize(code), plugin);
        if (ObjectUtil.isNotNull(previous)) {
            throw new IllegalStateException(
                    "Duplicate bot command code [%s]: %s / %s".formatted(
                            code,
                            previous.getClass().getName(),
                            plugin.getClass().getName()));
        }
    }

    private static void registerLookup(Map<String, Lookup> lookups, Lookup lookup) {
        Lookup previous = lookups.put(lookup.normalizedAlias(), lookup);
        if (ObjectUtil.isNotNull(previous) && previous.command() != lookup.command()) {
            throw new IllegalStateException(
                    "Duplicate bot command lookup name [%s]: %s / %s".formatted(
                            lookup.alias(),
                            previous.command().plugin().getClass().getName(),
                            lookup.command().plugin().getClass().getName()));
        }
    }

    private static CommandMatch toMatch(
            BotMessageContext context, Lookup lookup, String arguments) {
        RuntimeCommand command = lookup.command();
        return new CommandMatch(
                command,
                new CommandInvocation(
                        context,
                        command.descriptor().code(),
                        lookup.alias(),
                        arguments));
    }

    private static String prefixArguments(
            String message, String alias, CommandBoundaryPolicy boundaryPolicy) {
        if (!message.startsWith(alias)) {
            return null;
        }
        if (message.length() == alias.length()) {
            return "";
        }
        int next = message.codePointAt(alias.length());
        if (!allowsBoundary(boundaryPolicy, next)) {
            return null;
        }
        return message.substring(alias.length()).trim();
    }

    private static boolean allowsBoundary(CommandBoundaryPolicy policy, int next) {
        return switch (policy) {
            case NONE -> true;
            case WHITESPACE_REQUIRED -> Character.isWhitespace(next);
            case CJK_OR_WHITESPACE -> Character.isWhitespace(next) || isCjk(next);
        };
    }

    private static boolean isCjk(int codePoint) {
        UnicodeScript script = Character.UnicodeScript.of(codePoint);
        return script == Character.UnicodeScript.HAN
                || script == Character.UnicodeScript.HIRAGANA
                || script == Character.UnicodeScript.KATAKANA
                || script == Character.UnicodeScript.HANGUL;
    }

    private void refreshSafely() {
        try {
            refresh();
        } catch (RuntimeException exception) {
            log.error("Failed to refresh command registry; keeping previous snapshot", exception);
        }
    }

    private static String requireText(String value, String field) {
        String normalized = Objects.requireNonNull(
                value, field + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalStateException(field + " must not be blank");
        }
        return normalized;
    }

    private static String normalize(String value) {
        return ObjectUtil.isNull(value) ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private record Lookup(
            RuntimeCommand command,
            String alias,
            String normalizedAlias,
            CommandBoundaryPolicy boundaryPolicy
    ) {
    }

    private record Snapshot(
            List<RuntimeCommand> commands,
            Map<String, RuntimeCommand> commandsByCode,
            Map<String, Lookup> exactCommands,
            List<Lookup> prefixCommands
    ) {

        private static Snapshot empty() {
            return new Snapshot(List.of(), Map.of(), Map.of(), List.of());
        }
    }
}
