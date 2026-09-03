package com.nnm.bot.admin.adapter.command;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.admin.domain.BotCommand;
import com.nnm.bot.admin.domain.BotCommandCost;
import com.nnm.bot.admin.repository.BotCommandCostRepository;
import com.nnm.bot.admin.repository.BotCommandRepository;
import com.nnm.bot.core.plugin.model.CommandCostConfiguration;
import com.nnm.bot.core.plugin.api.CommandCostConfigurationProvider;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 从管理端命令配置表提供只读积分配置。
 */
@Component
public class DatabaseCommandCostConfigurationProvider
        implements CommandCostConfigurationProvider {

    @Resource
    private BotCommandRepository commandRepository;
    @Resource
    private BotCommandCostRepository costRepository;

    @Override
    public CommandCostConfiguration configuration(String commandCode) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(commandCode).orElse(null);
        if (ObjectUtil.isNull(command)) {
            return CommandCostConfiguration.free();
        }
        BotCommandCost cost = costRepository.findByCommandId(command.getId()).orElse(null);
        if (ObjectUtil.isNull(cost)) {
            return CommandCostConfiguration.free();
        }
        return new CommandCostConfiguration(
                cost.isEnabled(),
                cost.getCostPoints(),
                cost.isRewardEnabled(),
                cost.getRewardPoints());
    }
}
