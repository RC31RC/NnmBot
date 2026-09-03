package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 钢铁路径奖励命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.STEEL_PATH, name = "钢铁奖励", gameCode = "warframe", order = 51)
public class SteelPathPlugin extends AbstractWorldStateCommandPlugin {

    public SteelPathPlugin() {
        super(WarframeCommandData::steelPath);
    }
}
