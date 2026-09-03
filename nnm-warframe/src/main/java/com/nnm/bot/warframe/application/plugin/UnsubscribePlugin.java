package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 取消 Warframe 事件订阅。 */
@BotPluginCommand(
        code = WarframeCommandCodes.UNSUBSCRIBE,
        matchType = MatchType.PREFIX,
        name = "取消订阅",
        gameCode = "warframe",
        order = 76)
public final class UnsubscribePlugin extends DataSourceCommandPlugin {

    public UnsubscribePlugin() {
    }
}
