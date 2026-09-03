package com.nnm.bot.admin.adapter.command;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.admin.domain.BotCommand;
import com.nnm.bot.admin.domain.BotCommandAlias;
import com.nnm.bot.admin.domain.BotCommandChannel;
import com.nnm.bot.admin.domain.BotCommandTemplate;
import com.nnm.bot.admin.repository.BotCommandAliasRepository;
import com.nnm.bot.admin.repository.BotCommandChannelRepository;
import com.nnm.bot.admin.repository.BotCommandRepository;
import com.nnm.bot.admin.repository.BotCommandTemplateRepository;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.ChannelOutputMode;
import com.nnm.bot.core.plugin.api.CommandConfigurationProvider;
import com.nnm.bot.core.plugin.model.CommandDefinition;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandTrigger;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 批量加载数据库命令定义，避免消息执行链产生逐命令和逐消息查询。
 */
@Slf4j
@Component
public class DatabaseCommandConfigurationProvider implements CommandConfigurationProvider {

    @Resource
    private BotCommandRepository commandRepository;
    @Resource
    private BotCommandAliasRepository aliasRepository;
    @Resource
    private BotCommandChannelRepository channelRepository;
    @Resource
    private BotCommandTemplateRepository templateRepository;

    @Override
    public Map<String, CommandDefinition> loadAll() {
        List<BotCommand> commands = commandRepository.findAll();
        if (commands.isEmpty()) {
            return Map.of();
        }

        Map<Long, List<CommandTrigger>> triggersByCommand = triggersByCommand();
        Map<Long, Map<BotPlatform, ChannelOutputMode>> channelsByCommand =
                channelsByCommand();
        Map<Long, List<CommandExecutionConfiguration.CommandTemplate>> templatesByCommand =
                templatesByCommand();
        Map<String, CommandDefinition> definitions = new LinkedHashMap<>();

        for (BotCommand command : commands) {
            CommandExecutionConfiguration configuration =
                    new CommandExecutionConfiguration(
                            command.isEnabled(),
                            command.isShowInHelp(),
                            command.getOutputMode(),
                            templatesByCommand.getOrDefault(command.getId(), List.of()),
                            normalizedTriggers(triggersByCommand.get(command.getId())),
                            command.getPermissionOverride(),
                            command.getCooldownSeconds(),
                            command.isMentionInvoker(),
                            channelsByCommand.getOrDefault(command.getId(), Map.of()));
            CommandDefinition definition = new CommandDefinition(
                    command.getCode(),
                    command.getName(),
                    command.getGameCode(),
                    command.getDescription(),
                    configuration);
            definitions.put(command.getCode().trim().toLowerCase(Locale.ROOT), definition);
        }
        return Map.copyOf(definitions);
    }

    private Map<Long, List<CommandTrigger>> triggersByCommand() {
        Map<Long, List<CommandTrigger>> result = new HashMap<>();
        for (BotCommandAlias alias : aliasRepository.findAll()) {
            result.computeIfAbsent(alias.getCommandId(), ignored -> new ArrayList<>())
                    .add(new CommandTrigger(alias.getAliasName(), alias.getBoundaryPolicy()));
        }
        return result;
    }

    private Map<Long, Map<BotPlatform, ChannelOutputMode>> channelsByCommand() {
        Map<Long, Map<BotPlatform, ChannelOutputMode>> result = new HashMap<>();
        for (BotCommandChannel channel : channelRepository.findAll()) {
            try {
                BotPlatform platform = BotPlatform.valueOf(
                        channel.getPlatform().trim().toUpperCase(Locale.ROOT));
                result.computeIfAbsent(
                                channel.getCommandId(),
                                ignored -> new EnumMap<>(BotPlatform.class))
                        .put(platform, channel.getOutputMode());
            } catch (IllegalArgumentException exception) {
                log.warn("Ignoring unsupported command channel platform [{}]",
                        channel.getPlatform());
            }
        }
        return result;
    }

    private Map<Long, List<CommandExecutionConfiguration.CommandTemplate>> templatesByCommand() {
        Map<Long, List<BotCommandTemplate>> templates = new HashMap<>();
        for (BotCommandTemplate template : templateRepository.findAll()) {
            if (template.isEnabled() && ObjectUtil.isNotNull(template.getCommandId())) {
                templates.computeIfAbsent(template.getCommandId(), ignored -> new ArrayList<>())
                        .add(template);
            }
        }
        Map<Long, List<CommandExecutionConfiguration.CommandTemplate>> result = new HashMap<>();
        templates.forEach((commandId, items) -> {
            items.sort(java.util.Comparator
                    .comparingInt(BotCommandTemplate::getSortOrder)
                    .thenComparing(BotCommandTemplate::getId));
            result.put(commandId, items.stream()
                    .map(template -> new CommandExecutionConfiguration.CommandTemplate(
                            template.getTemplateCode(), template.getHtmlContent()))
                    .toList());
        });
        return result;
    }

    private List<CommandTrigger> normalizedTriggers(List<CommandTrigger> triggers) {
        if (triggers == null || triggers.isEmpty()) {
            return List.of();
        }
        Map<String, CommandTrigger> unique = new LinkedHashMap<>();
        for (CommandTrigger trigger : triggers) {
            unique.putIfAbsent(trigger.name().toLowerCase(Locale.ROOT), trigger);
        }
        return List.copyOf(unique.values());
    }

}
