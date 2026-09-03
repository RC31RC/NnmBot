package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 更新市场物品基础数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.UPDATE_MARKET_ITEMS,
        matchType = MatchType.EXACT,
        name = "更新WM物品",
        gameCode = "warframe",
        order = 58)
public final class UpdateMarketItemsPlugin extends DataSourceCommandPlugin {

    public UpdateMarketItemsPlugin() {
    }
}
