package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 当前电波任务命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.NIGHTWAVE, name = "电波", gameCode = "warframe", order = 57)
public class NightwavePlugin extends AbstractWorldStateCommandPlugin {

    public NightwavePlugin() {
        super(WarframeCommandData::nightwave);
    }
}
