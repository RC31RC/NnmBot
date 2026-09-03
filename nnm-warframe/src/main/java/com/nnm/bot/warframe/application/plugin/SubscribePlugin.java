package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 创建 Warframe 事件订阅。 */
@BotPluginCommand(code = WarframeCommandCodes.SUBSCRIBE, matchType = MatchType.PREFIX, name = "订阅", gameCode = "warframe", order = 75)
public final class SubscribePlugin extends DataSourceCommandPlugin {

    public SubscribePlugin() {
    }
}
