package com.nnm.bot.core.plugin.builtin;

import com.nnm.bot.core.message.BotMessageContext;
import com.nnm.bot.core.message.BotMessageSender;
import com.nnm.bot.core.plugin.BotPlugin;
import com.nnm.bot.core.plugin.CommandDescriptor;
import com.nnm.bot.core.plugin.CommandRegistry;
import com.nnm.bot.core.plugin.MatchType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Locale;
import java.util.Set;

@Component
public class HelpPlugin implements BotPlugin {

    private final BotMessageSender messageSender;
    private final ObjectProvider<CommandRegistry> commandRegistries;

    public HelpPlugin(BotMessageSender messageSender,
                      ObjectProvider<CommandRegistry> commandRegistries) {
        this.messageSender = messageSender;
        this.commandRegistries = commandRegistries;
    }

    @Override
    public boolean match(String message) {
        return message != null && descriptor().lookupNames().stream()
                .anyMatch(name -> name.trim().toLowerCase(Locale.ROOT)
                        .equals(message.trim().toLowerCase(Locale.ROOT)));
    }

    @Override
    public void handle(BotMessageContext context) {
        StringBuilder help = new StringBuilder("NnmBot 命令\n");
        commandRegistries.getObject().descriptors().stream()
                .filter(descriptor -> !descriptor.code().equals(descriptor().code()))
                .sorted(Comparator.comparingInt(CommandDescriptor::order)
                        .thenComparing(descriptor ->
                                descriptor.code().trim().toLowerCase(java.util.Locale.ROOT)))
                .forEach(descriptor -> help.append('\n')
                        .append(descriptor.name())
                        .append(" - ")
                        .append(descriptor.description()));
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

    @Override
    public CommandDescriptor descriptor() {
        return new CommandDescriptor(
                "help",
                "帮助",
                Set.of("help", "指令", "命令", "菜单"),
                description(),
                "",
                MatchType.EXACT,
                order()
        );
    }
}
