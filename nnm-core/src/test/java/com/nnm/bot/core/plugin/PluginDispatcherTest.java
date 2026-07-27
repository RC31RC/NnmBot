package com.nnm.bot.core.plugin;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PluginDispatcherTest {

    @Test
    void sortsPluginsAndDispatchesOnlyTheFirstMatch() {
        List<String> calls = new ArrayList<>();
        BotPlugin later = plugin("later", 20, true, calls);
        BotPlugin first = plugin("first", 10, true, calls);

        PluginDispatcher dispatcher = new PluginDispatcher(List.of(later, first));
        dispatcher.dispatch(null, null, "message");

        assertThat(dispatcher.plugins()).extracting(BotPlugin::command)
                .containsExactly("first", "later");
        assertThat(calls).containsExactly("first");
    }

    @Test
    void rejectsDuplicateCommandsIgnoringCaseAndWhitespace() {
        BotPlugin first = plugin("Status", 0, false, new ArrayList<>());
        BotPlugin duplicate = plugin(" status ", 10, false, new ArrayList<>());

        assertThatThrownBy(() -> new PluginDispatcher(List.of(first, duplicate)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate bot plugin command");
    }

    @Test
    void continuesWhenAPluginMatcherFails() {
        List<String> calls = new ArrayList<>();
        BotPlugin broken = new TestPlugin("broken", 0, false, calls) {
            @Override
            public boolean match(String message) {
                throw new IllegalStateException("broken matcher");
            }
        };
        BotPlugin fallback = plugin("fallback", 10, true, calls);

        new PluginDispatcher(List.of(broken, fallback)).dispatch(null, null, "message");

        assertThat(calls).containsExactly("fallback");
    }

    @Test
    void doesNotDispatchAgainWhenMatchedPluginHandlerFails() {
        List<String> calls = new ArrayList<>();
        BotPlugin broken = new TestPlugin("broken", 0, true, calls) {
            @Override
            public void handle(Bot bot, AnyMessageEvent event, String message) {
                calls.add(command());
                throw new IllegalStateException("broken handler");
            }
        };
        BotPlugin fallback = plugin("fallback", 10, true, calls);

        new PluginDispatcher(List.of(broken, fallback)).dispatch(null, null, "message");

        assertThat(calls).containsExactly("broken");
    }

    private static BotPlugin plugin(
            String command, int order, boolean matches, List<String> calls) {
        return new TestPlugin(command, order, matches, calls);
    }

    private static class TestPlugin implements BotPlugin {

        private final String command;
        private final int order;
        private final boolean matches;
        private final List<String> calls;

        private TestPlugin(String command, int order, boolean matches, List<String> calls) {
            this.command = command;
            this.order = order;
            this.matches = matches;
            this.calls = calls;
        }

        @Override
        public boolean match(String message) {
            return matches;
        }

        @Override
        public void handle(Bot bot, AnyMessageEvent event, String message) {
            calls.add(command);
        }

        @Override
        public String command() {
            return command;
        }

        @Override
        public String description() {
            return command;
        }

        @Override
        public int order() {
            return order;
        }
    }
}
