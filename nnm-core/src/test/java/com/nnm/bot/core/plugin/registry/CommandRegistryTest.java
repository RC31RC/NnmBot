package com.nnm.bot.core.plugin.registry;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.CommandBoundaryPolicy;
import com.nnm.bot.core.plugin.model.PresentationPolicy;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.api.CommandConfigurationProvider;
import com.nnm.bot.core.plugin.model.CommandDefinition;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandTrigger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommandRegistryTest {

    @Test
    void registersPluginsWithoutDatabaseDefinitions() {
        CommandRegistry registry = registry(
                List.of(plugin("status", MatchType.EXACT)),
                new MutableConfigurationProvider());

        registry.initialize();

        assertThat(registry.size()).isEqualTo(1);
        assertThat(registry.command("status")).isPresent();
        assertThat(registry.match(context("status"))).isPresent();
    }

    @Test
    void rejectsDuplicateCommandCodes() {
        CommandRegistry registry = registry(
                List.of(plugin("same", MatchType.EXACT),
                        plugin("same", MatchType.EXACT)),
                new MutableConfigurationProvider());

        assertThatThrownBy(registry::initialize)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate bot command code");
    }

    @Test
    void rejectsDuplicateDatabaseLookupNames() {
        MutableConfigurationProvider provider = new MutableConfigurationProvider();
        provider.put("first", "命令一", trigger("公共别名"));
        provider.put("second", "命令二", trigger("公共别名"));
        CommandRegistry registry = registry(
                List.of(plugin("first", MatchType.EXACT),
                        plugin("second", MatchType.EXACT)),
                provider);

        assertThatThrownBy(registry::initialize)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate bot command lookup name");
    }

    @Test
    void routesDatabaseNameAndAlias() {
        MutableConfigurationProvider provider = new MutableConfigurationProvider();
        provider.put("status", "状态", trigger("运行情况"));
        CommandRegistry registry =
                registry(List.of(plugin("status", MatchType.EXACT)), provider);
        registry.initialize();

        assertThat(registry.match(context(" 状态 ")))
                .get()
                .extracting(match -> match.invocation().matchedAlias())
                .isEqualTo("状态");
        assertThat(registry.match(context("运行情况")))
                .get()
                .extracting(match -> match.command().descriptor().code())
                .isEqualTo("status");
    }

    @Test
    void extractsPrefixArgumentsFromDatabaseAlias() {
        MutableConfigurationProvider provider = new MutableConfigurationProvider();
        provider.put("market", "市场", trigger("wm"));
        CommandRegistry registry =
                registry(List.of(plugin("market", MatchType.PREFIX)), provider);
        registry.initialize();

        CommandMatch match = registry.match(context("wm 满级 pc broken war")).orElseThrow();

        assertThat(match.invocation().matchedAlias()).isEqualTo("wm");
        assertThat(match.invocation().arguments()).isEqualTo("满级 pc broken war");
        assertThat(registry.match(context("wmarket"))).isEmpty();
    }

    @Test
    void routesZkAliasToRivenMarketCommand() {
        MutableConfigurationProvider provider = new MutableConfigurationProvider();
        provider.put("market", "紫卡市场", trigger("zk"));
        CommandRegistry registry =
                registry(List.of(plugin("market", MatchType.PREFIX)), provider);
        registry.initialize();

        CommandMatch match = registry.match(
                context("zk 拉特昂 多重 暴击 爆伤 负任意")).orElseThrow();

        assertThat(match.invocation().matchedAlias()).isEqualTo("zk");
        assertThat(match.invocation().arguments())
                .isEqualTo("拉特昂 多重 暴击 爆伤 负任意");
        assertThat(match.command().descriptor().code()).isEqualTo("market");
    }

    @Test
    void cjkOrWhitespaceBoundaryAllowsChineseArgumentsButRejectsAsciiConcatenation() {
        MutableConfigurationProvider provider = new MutableConfigurationProvider();
        provider.put("market", "市场", List.of(new CommandTrigger(
                "wm", CommandBoundaryPolicy.CJK_OR_WHITESPACE)));
        CommandRegistry registry =
                registry(List.of(plugin("market", MatchType.PREFIX)), provider);
        registry.initialize();

        assertThat(registry.match(context("wm拉特昂")))
                .get()
                .extracting(match -> match.invocation().arguments())
                .isEqualTo("拉特昂");
        assertThat(registry.match(context("wm 拉特昂")))
                .get()
                .extracting(match -> match.invocation().arguments())
                .isEqualTo("拉特昂");
        assertThat(registry.match(context("wmanything"))).isEmpty();
    }

    @Test
    void whitespaceBoundaryRequiresASeparatorBeforeArguments() {
        MutableConfigurationProvider provider = new MutableConfigurationProvider();
        provider.put("market", "市场", List.of(new CommandTrigger(
                "wm", CommandBoundaryPolicy.WHITESPACE_REQUIRED)));
        CommandRegistry registry =
                registry(List.of(plugin("market", MatchType.PREFIX)), provider);
        registry.initialize();

        assertThat(registry.match(context("wm拉特昂"))).isEmpty();
        assertThat(registry.match(context("wm 拉特昂"))).isPresent();
    }

    @Test
    void noneBoundaryAllowsArgumentsWithoutASeparator() {
        MutableConfigurationProvider provider = new MutableConfigurationProvider();
        provider.put("market", "市场", List.of(new CommandTrigger(
                "wm", CommandBoundaryPolicy.NONE)));
        CommandRegistry registry =
                registry(List.of(plugin("market", MatchType.PREFIX)), provider);
        registry.initialize();

        assertThat(registry.match(context("wmanything")))
                .get()
                .extracting(match -> match.invocation().arguments())
                .isEqualTo("anything");
    }

    @Test
    void chineseDisplayNameUsesImplicitNoBoundaryPolicy() {
        MutableConfigurationProvider provider = new MutableConfigurationProvider();
        provider.put("market", "市场", List.of());
        CommandRegistry registry =
                registry(List.of(plugin("market", MatchType.PREFIX)), provider);
        registry.initialize();

        assertThat(registry.match(context("市场拉特昂")))
                .get()
                .extracting(match -> match.invocation().arguments())
                .isEqualTo("拉特昂");
    }

    @Test
    void refreshesConfigurationWithoutRebuildingPlugins() {
        MutableConfigurationProvider provider = new MutableConfigurationProvider();
        provider.put("status", "状态", List.of());
        BotPlugin plugin = plugin("status", MatchType.EXACT);
        CommandRegistry registry = registry(List.of(plugin), provider);
        registry.initialize();

        assertThat(registry.match(context("运行情况"))).isEmpty();

        provider.put("status", "状态", trigger("运行情况"));
        registry.refresh();

        assertThat(registry.match(context("运行情况")))
                .get()
                .extracting(match -> match.command().plugin())
                .isSameAs(plugin);
    }

    @Test
    void failedRefreshKeepsPreviousSnapshot() {
        MutableConfigurationProvider provider = new MutableConfigurationProvider();
        provider.put("first", "命令一", List.of());
        provider.put("second", "命令二", List.of());
        CommandRegistry registry = registry(
                List.of(plugin("first", MatchType.EXACT),
                        plugin("second", MatchType.EXACT)),
                provider);
        registry.initialize();

        provider.put("first", "命令一", trigger("冲突"));
        provider.put("second", "命令二", trigger("冲突"));
        assertThatThrownBy(registry::refresh)
                .isInstanceOf(IllegalStateException.class);

        assertThat(registry.match(context("命令一"))).isPresent();
        assertThat(registry.match(context("冲突"))).isEmpty();
    }

    private static CommandRegistry registry(
            List<BotPlugin> plugins, CommandConfigurationProvider provider) {
        StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
        beanFactory.addBean("commandConfigurationProvider", provider);
        CommandRegistry registry = new CommandRegistry();
        ReflectionTestUtils.setField(registry, "botPlugins", plugins);
        ReflectionTestUtils.setField(
                registry,
                "configurationProvider",
                new DefaultListableBeanFactory(beanFactory)
                        .getBeanProvider(CommandConfigurationProvider.class));
        return registry;
    }

    private static BotPlugin plugin(String code, MatchType matchType) {
        if (matchType == MatchType.EXACT) {
            return switch (code) {
                case "status" -> new StatusTestPlugin();
                case "first" -> new FirstTestPlugin();
                case "second" -> new SecondTestPlugin();
                case "same" -> new SameTestPlugin();
                default -> throw new IllegalArgumentException("Unknown exact test plugin: " + code);
            };
        }
        if ("market".equals(code) && matchType == MatchType.PREFIX) {
            return new MarketTestPlugin();
        }
        throw new IllegalArgumentException("Unknown test plugin: " + code);
    }

    private abstract static class TestPlugin extends BotPlugin {

        private final String code;

        private TestPlugin(String code) {
            this.code = code;
        }

        @Override
        public CommandResult handle(CommandInvocation invocation) {
            return CommandResult.text(code);
        }
    }

    @BotPluginCommand(
            code = "status", name = "status", gameCode = "system", order = 1)
    private static final class StatusTestPlugin extends TestPlugin {
        private StatusTestPlugin() {
            super("status");
        }
    }

    @BotPluginCommand(
            code = "first", name = "命令一", gameCode = "system", order = 1)
    private static final class FirstTestPlugin extends TestPlugin {
        private FirstTestPlugin() {
            super("first");
        }
    }

    @BotPluginCommand(
            code = "second", name = "命令二", gameCode = "system", order = 2)
    private static final class SecondTestPlugin extends TestPlugin {
        private SecondTestPlugin() {
            super("second");
        }
    }

    @BotPluginCommand(
            code = "same", name = "same", gameCode = "system", order = 1)
    private static final class SameTestPlugin extends TestPlugin {
        private SameTestPlugin() {
            super("same");
        }
    }

    @BotPluginCommand(
            code = "market",
            matchType = MatchType.PREFIX,
            name = "market",
            gameCode = "warframe",
            order = 1)
    private static final class MarketTestPlugin extends TestPlugin {
        private MarketTestPlugin() {
            super("market");
        }
    }

    private static BotMessageContext context(String message) {
        return BotMessageContext.of(
                BotPlatform.ONEBOT,
                BotMessageScope.GROUP,
                "10001",
                "20001",
                "30001",
                "40001",
                message,
                message,
                new Object());
    }

    private static List<CommandTrigger> trigger(String name) {
        return List.of(new CommandTrigger(name, CommandBoundaryPolicy.defaultFor(name)));
    }

    private static final class MutableConfigurationProvider
            implements CommandConfigurationProvider {

        private final Map<String, CommandDefinition> definitions = new LinkedHashMap<>();

        private void put(String code, String name, List<CommandTrigger> triggers) {
            definitions.put(code, new CommandDefinition(
                    code,
                    name,
                    "system",
                    name,
                    new CommandExecutionConfiguration(
                            true,
                            true,
                            PresentationPolicy.defaults(),
                            List.of(),
                            triggers,
                            "",
                            0,
                            false)));
        }

        @Override
        public Map<String, CommandDefinition> loadAll() {
            return Map.copyOf(definitions);
        }
    }
}
