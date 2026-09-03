package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询紫卡市场数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.MARKET_RIVEN,
        matchType = MatchType.PREFIX,
        name = "紫卡市场",
        gameCode = "warframe",
        order = 64)
public final class MarketRivenPlugin extends DataSourceCommandPlugin {

    public MarketRivenPlugin() {
    }
}
