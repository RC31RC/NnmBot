package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询赤毒相关数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.LICHES,
        matchType = MatchType.PREFIX,
        name = "赤毒",
        gameCode = "warframe",
        order = 66)
public final class LichesPlugin extends DataSourceCommandPlugin {

    public LichesPlugin() {
    }
}
