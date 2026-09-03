package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 更新信条和赤毒数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.UPDATE_SISTER,
        matchType = MatchType.EXACT,
        name = "更新信条",
        gameCode = "warframe",
        order = 60)
public final class UpdateSisterPlugin extends DataSourceCommandPlugin {

    public UpdateSisterPlugin() {
    }
}
