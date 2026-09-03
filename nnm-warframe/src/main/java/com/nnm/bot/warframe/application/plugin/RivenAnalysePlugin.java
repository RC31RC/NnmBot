package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 分析紫卡截图或文本。 */
@BotPluginCommand(
        code = WarframeCommandCodes.RIVEN_ANALYSE,
        matchType = MatchType.PREFIX,
        name = "紫卡分析",
        gameCode = "warframe",
        order = 73)
public final class RivenAnalysePlugin extends DataSourceCommandPlugin {

    public RivenAnalysePlugin() {
    }
}
