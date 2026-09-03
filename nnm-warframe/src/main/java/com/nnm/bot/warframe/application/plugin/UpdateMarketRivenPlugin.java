package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 更新市场紫卡基础数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.UPDATE_MARKET_RIVEN,
        matchType = MatchType.EXACT,
        name = "更新WM紫卡",
        gameCode = "warframe",
        order = 59)
public final class UpdateMarketRivenPlugin extends DataSourceCommandPlugin {

    public UpdateMarketRivenPlugin() {
    }
}
