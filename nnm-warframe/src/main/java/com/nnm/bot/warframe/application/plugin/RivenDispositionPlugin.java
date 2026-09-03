package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查看紫卡倾向变化。 */
@BotPluginCommand(
        code = WarframeCommandCodes.RIVEN_DISPOSITION,
        matchType = MatchType.EXACT,
        name = "紫卡倾向变动",
        gameCode = "warframe",
        order = 62)
public final class RivenDispositionPlugin extends DataSourceCommandPlugin {

    public RivenDispositionPlugin() {
    }
}
