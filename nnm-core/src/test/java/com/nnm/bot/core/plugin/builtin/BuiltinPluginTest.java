package com.nnm.bot.core.plugin.builtin;

import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.message.BotMessageSender;
import com.nnm.bot.core.plugin.BotPlugin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BuiltinPluginTest {

    @Test
    void helpListsLoadedPlugins() {
        BotMessageSender sender = mock(BotMessageSender.class);
        BotMessageContext context = mock(BotMessageContext.class);
        BotPlugin later = plugin("Zeta", "第二个命令");
        BotPlugin first = plugin(" alpha ", "第一个命令");
        ObjectProvider<BotPlugin> provider = provider(later, first);
        HelpPlugin help = new HelpPlugin(sender, provider);

        help.handle(context);

        verify(sender).sendText(context,
                "NnmBot 命令\n\nalpha - 第一个命令\nZeta - 第二个命令\n\n帮助 - 查看本菜单");
        assertThat(help.match(" HELP ")).isTrue();
        assertThat(help.match("未知")).isFalse();
    }

    @Test
    void statusReportsRuntimeAndBotInformation() {
        BotMessageSender sender = mock(BotMessageSender.class);
        BotMessageContext context = mock(BotMessageContext.class);
        when(context.botId()).thenReturn(10001L);
        when(context.isGroupMessage()).thenReturn(true);
        StatusPlugin status = new StatusPlugin(sender, provider(plugin("帮助", "查看命令菜单")));

        status.handle(context);

        verify(sender).sendText(org.mockito.ArgumentMatchers.eq(context),
                org.mockito.ArgumentMatchers.argThat(message ->
                        message.contains("NnmBot 运行正常")
                                && message.contains("机器人 QQ：10001")
                                && message.contains("会话类型：群聊")
                                && message.contains("插件数量：1")));
        assertThat(status.match("运行状态")).isTrue();
        assertThat(status.match("status")).isFalse();
    }

    private static BotPlugin plugin(String command, String description) {
        BotPlugin plugin = mock(BotPlugin.class);
        when(plugin.command()).thenReturn(command);
        when(plugin.description()).thenReturn(description);
        return plugin;
    }

    @SuppressWarnings("unchecked")
    private static ObjectProvider<BotPlugin> provider(BotPlugin... plugins) {
        ObjectProvider<BotPlugin> provider = mock(ObjectProvider.class);
        when(provider.stream()).thenAnswer(ignored -> Stream.of(plugins));
        return provider;
    }
}
