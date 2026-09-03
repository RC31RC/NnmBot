package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 当前警报命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.ALERTS, name = "警报", gameCode = "warframe", order = 40)
public class AlertsPlugin extends AbstractWorldStateCommandPlugin {

    public AlertsPlugin() {
        super(WarframeCommandData::alerts);
    }
}
