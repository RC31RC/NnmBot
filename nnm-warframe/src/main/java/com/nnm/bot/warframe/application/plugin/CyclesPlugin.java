package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 开放世界周期命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.CYCLES, name = "平原", gameCode = "warframe", order = 52)
public class CyclesPlugin extends AbstractWorldStateCommandPlugin {

    public CyclesPlugin() {
        super(WarframeCommandData::cycles);
    }
}
