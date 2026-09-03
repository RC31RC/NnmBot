package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 每日特惠命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.DAILY_DEALS, name = "每日特惠", gameCode = "warframe", order = 46)
public class DailyDealsPlugin extends AbstractWorldStateCommandPlugin {

    public DailyDealsPlugin() {
        super(WarframeCommandData::dailyDeals);
    }
}
