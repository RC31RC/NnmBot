package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询紫卡交易订单。 */
@BotPluginCommand(
        code = WarframeCommandCodes.RIVEN_MARKET,
        matchType = MatchType.PREFIX,
        name = "紫卡市场查询",
        gameCode = "warframe",
        order = 65)
public final class RivenMarketPlugin extends DataSourceCommandPlugin {

    public RivenMarketPlugin() {
    }
}
