package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/**
 * 仲裁命令。真实数据由官方 WorldState 适配层提供。
 */
@BotPluginCommand(
        code = WarframeCommandCodes.ARBITRATION, name = "仲裁", gameCode = "warframe", order = 30)
public class ArbitrationPlugin extends AbstractWorldStateCommandPlugin {

    public ArbitrationPlugin() {
        super(
                state -> WarframeCommandData.arbitration(state, false)
        );
    }
}
