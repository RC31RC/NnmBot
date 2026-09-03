package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 虚空商人行踪和商品命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.VOID_TRADER, name = "奸商", gameCode = "warframe", order = 43)
public class VoidTraderPlugin extends AbstractWorldStateCommandPlugin {

    public VoidTraderPlugin() {
        super(WarframeCommandData::voidTrader);
    }
}
