package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查看游戏 1999 相关日历数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.CALENDAR_1999,
        name = "1999日历",
        gameCode = "warframe",
        order = 74)
public final class Calendar1999Plugin extends DataSourceCommandPlugin {

    public Calendar1999Plugin() {
    }
}
