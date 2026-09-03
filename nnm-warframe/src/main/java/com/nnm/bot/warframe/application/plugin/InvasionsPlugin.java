package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 当前入侵命令。 */
@BotPluginCommand(code = WarframeCommandCodes.INVASIONS, name = "入侵", gameCode = "warframe", order = 47)
public class InvasionsPlugin extends AbstractWorldStateCommandPlugin {

    public InvasionsPlugin() {
        super(WarframeCommandData::invasions);
    }
}
