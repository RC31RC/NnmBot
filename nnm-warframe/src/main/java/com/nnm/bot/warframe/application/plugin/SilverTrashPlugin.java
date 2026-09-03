package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询银色遗物奖励推荐。 */
@BotPluginCommand(
        code = WarframeCommandCodes.SILVER_TRASH,
        matchType = MatchType.EXACT,
        name = "银垃圾",
        gameCode = "warframe",
        order = 70)
public final class SilverTrashPlugin extends DataSourceCommandPlugin {

    public SilverTrashPlugin() {
    }
}
