package com.nnm.bot.core.plugin.builtin;

import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.message.BotMessageSender;
import com.nnm.bot.core.plugin.BotPlugin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Locale;
import java.util.Set;

@Component
public class HelpPlugin implements BotPlugin {

    private static final Set<String> COMMANDS = Set.of("帮助", "help", "指令", "命令", "菜单");

    private final BotMessageSender messageSender;
    private final ObjectProvider<BotPlugin> plugins;

    public HelpPlugin(BotMessageSender messageSender, ObjectProvider<BotPlugin> plugins) {
        this.messageSender = messageSender;
        this.plugins = plugins;
    }

    @Override
    public boolean match(String message) {
        return message != null && COMMANDS.contains(message.trim().toLowerCase(Locale.ROOT));
    }

    @Override
    public void handle(BotMessageContext context) {
        StringBuilder help = new StringBuilder("NnmBot 命令\n");
        plugins.stream()
                .filter(plugin -> plugin != this)
                .sorted(Comparator.comparingInt(BotPlugin::order)
                        .thenComparing(plugin ->
                                plugin.command().trim().toLowerCase(Locale.ROOT)))
                .forEach(plugin -> help.append('\n')
                        .append(plugin.command().trim())
                        .append(" - ")
                        .append(plugin.description()));
        help.append("\n\n帮助 - 查看本菜单");
        messageSender.sendText(context, help.toString());
    }

    @Override
    public String command() {
        return "帮助";
    }

    @Override
    public String description() {
        return "查看命令菜单";
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }
}
