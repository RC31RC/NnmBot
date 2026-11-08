package com.nnm.bot.admin.adapter.command;

import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration.CommandTemplate;
import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommand;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandAlias;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandTemplate;
import com.nnm.bot.admin.repository.bot.command.BotCommandAliasRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandTemplateRepository;
import com.nnm.bot.core.plugin.api.CommandConfigurationProvider;
import com.nnm.bot.core.plugin.model.CommandDefinition;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandTrigger;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private BotCommandTemplateRepository templateRepository;

    @Override
    public Map<String, CommandDefinition> loadAll() {
        List<BotCommand> commands = commandRepository.findAll();
        if (commands.isEmpty()) {
            return Map.of();
        }

        Map<Long, List<CommandTrigger>> triggersByCommand = triggersByCommand();
        Map<Long, CommandTemplate> templatesByCommand = templatesByCommand(commands);
        Map<String, CommandDefinition> definitions = new LinkedHashMap<>();

        for (BotCommand command : commands) {
            CommandExecutionConfiguration configuration =
                new CommandExecutionConfiguration(command.isEnabled(), command.isShowInHelp(),
                    templatesByCommand.get(command.getId()), normalizedTriggers(triggersByCommand.get(command.getId())),
                    command.getPermissionOverride(), command.getCooldownSeconds(), command.isMentionInvoker());
            CommandDefinition definition = new CommandDefinition(command.getCode(), command.getName(),
                command.getGameCode(), command.getDescription(), configuration);
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

    private Map<Long, CommandTemplate> templatesByCommand(List<BotCommand> commands) {
        Map<Long, BotCommandTemplate> templates = new HashMap<>();
        for (BotCommandTemplate template : templateRepository.findAll())
            templates.put(template.getId(), template);
        Map<Long, CommandTemplate> result = new HashMap<>();
        for (BotCommand command : commands) {
            Long templateId = command.getTemplateId();
            if (templateId == null)
                continue;
            BotCommandTemplate selected = templates.get(templateId);
            if (selected == null || !selected.isEnabled() || !command.getId().equals(selected.getCommandId())) {
                log.warn("Ignoring invalid selected HTML template commandId={} templateId={}", command.getId(),
                    templateId);
                continue;
            }
            result.put(command.getId(), new CommandTemplate(selected.getTemplateCode(), selected.getHtmlContent()));
        }
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
