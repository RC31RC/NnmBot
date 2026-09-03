package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询信条相关数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.SISTERS,
        matchType = MatchType.PREFIX,
        name = "信条",
        gameCode = "warframe",
        order = 67)
public final class SistersPlugin extends DataSourceCommandPlugin {

    public SistersPlugin() {
    }
}
