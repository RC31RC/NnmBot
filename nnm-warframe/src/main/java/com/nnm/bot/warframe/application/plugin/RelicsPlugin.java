package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询遗物及其奖励。 */
@BotPluginCommand(
        code = WarframeCommandCodes.RELICS,
        matchType = MatchType.PREFIX,
        name = "遗物",
        gameCode = "warframe",
        order = 71)
public final class RelicsPlugin extends DataSourceCommandPlugin {

    public RelicsPlugin() {
    }
}
