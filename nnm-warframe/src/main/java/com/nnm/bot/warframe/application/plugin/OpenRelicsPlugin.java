package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询遗物组队信息。 */
@BotPluginCommand(
        code = WarframeCommandCodes.OPEN_RELICS,
        matchType = MatchType.PREFIX,
        name = "开核桃",
        gameCode = "warframe",
        order = 72)
public final class OpenRelicsPlugin extends DataSourceCommandPlugin {

    public OpenRelicsPlugin() {
    }
}
