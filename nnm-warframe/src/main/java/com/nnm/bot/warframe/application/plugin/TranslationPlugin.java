package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询游戏文本翻译。 */
@BotPluginCommand(
        code = WarframeCommandCodes.TRANSLATION,
        matchType = MatchType.PREFIX,
        name = "翻译",
        gameCode = "warframe",
        order = 63)
public final class TranslationPlugin extends DataSourceCommandPlugin {

    public TranslationPlugin() {
    }
}
