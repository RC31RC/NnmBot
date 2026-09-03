package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 当前执刑官猎杀命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.ARCHON_HUNTS, name = "执刑官猎杀", gameCode = "warframe", order = 42)
public class ArchonHuntPlugin extends AbstractWorldStateCommandPlugin {

    public ArchonHuntPlugin() {
        super(
                state -> WarframeCommandData.sorties(state, true));
    }
}
