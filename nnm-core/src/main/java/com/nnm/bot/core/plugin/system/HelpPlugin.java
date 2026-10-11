package com.nnm.bot.core.plugin.system;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.core.constant.MatchType;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Comparator;
import java.util.Locale;

@BotPluginCommand(
        code = SystemCommandCodes.HELP,
        matchType = MatchType.EXACT,
        name = "帮助",
        gameCode = "system",
        order = Integer.MIN_VALUE)
public class HelpPlugin extends BotPlugin {

    @Resource
    private ObjectProvider<CommandRegistry> commandRegistryProvider;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        StringBuilder help = new StringBuilder("NnmBot 命令\n");
        this.commandRegistryProvider.getObject().commands().stream()
                .filter(command -> !command.descriptor().code().equalsIgnoreCase(SystemCommandCodes.HELP))
                .filter(command -> command.configuration().enabled())
                .filter(command -> command.configuration().showInHelp())
                .sorted(Comparator.comparingInt(
                                (RuntimeCommand command) -> command.descriptor().order())
                        .thenComparing(command ->
                                command.descriptor().code().toLowerCase(Locale.ROOT)))
                .map(RuntimeCommand::descriptor)
                .forEach(descriptor -> appendCommand(help, descriptor));
        help.append("\n\n帮助 - 查看本菜单");
        return CommandResult.text(help.toString());
    }

    private static void appendCommand(StringBuilder help, CommandDescriptor descriptor) {
        help.append('\n').append(descriptor.name());
        if (!descriptor.description().isBlank()) {
            help.append(" - ").append(descriptor.description());
        }
    }
}
