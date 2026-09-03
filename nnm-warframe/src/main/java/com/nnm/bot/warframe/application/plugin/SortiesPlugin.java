package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 当前突击命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.SORTIES, name = "突击", gameCode = "warframe", order = 41)
public class SortiesPlugin extends AbstractWorldStateCommandPlugin {

    public SortiesPlugin() {
        super(state -> WarframeCommandData.sorties(state, false));
    }
}
