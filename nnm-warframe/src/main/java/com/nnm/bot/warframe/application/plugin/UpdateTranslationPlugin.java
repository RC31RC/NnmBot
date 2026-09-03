package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 更新游戏文本翻译数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.UPDATE_TRANSLATION,
        matchType = MatchType.EXACT,
        name = "更新翻译",
        gameCode = "warframe",
        order = 61)
public final class UpdateTranslationPlugin extends DataSourceCommandPlugin {

    public UpdateTranslationPlugin() {
    }
}
