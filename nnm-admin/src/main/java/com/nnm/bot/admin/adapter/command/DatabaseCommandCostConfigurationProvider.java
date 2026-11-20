package com.nnm.bot.admin.adapter.command;

import cn.hutool.core.util.ObjUtil;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommand;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandCost;
import com.nnm.bot.admin.repository.bot.command.BotCommandCostRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandRepository;
import com.nnm.bot.core.plugin.api.CommandCostConfigurationDTO;
import com.nnm.bot.core.plugin.api.CommandCostConfigurationProvider;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 从管理端命令配置表提供只读积分配置。
 */
@Component
public class DatabaseCommandCostConfigurationProvider implements CommandCostConfigurationProvider {

    @Resource
    private BotCommandRepository commandRepository;
    @Resource
    private BotCommandCostRepository costRepository;

    @Override
    public CommandCostConfigurationDTO configuration(String commandCode) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(commandCode).orElse(null);
        if (ObjUtil.isNull(command)) {
            return CommandCostConfigurationDTO.free();
        }
        BotCommandCost cost = costRepository.findByCommandId(command.getId()).orElse(null);
        if (ObjUtil.isNull(cost)) {
            return CommandCostConfigurationDTO.free();
        }
        return new CommandCostConfigurationDTO(cost.isEnabled(), cost.getCostPoints(), cost.isRewardEnabled(),
            cost.getRewardPoints());
    }
}
