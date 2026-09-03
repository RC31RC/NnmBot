package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 仲裁轮换表命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.ARBITRATION_TABLE, name = "仲裁表", gameCode = "warframe", order = 45)
public class ArbitrationTablePlugin extends AbstractWorldStateCommandPlugin {

    public ArbitrationTablePlugin() {
        super(
                state -> WarframeCommandData.arbitration(state, true));
    }
}
