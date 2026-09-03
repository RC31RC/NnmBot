package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询阵营轮换与信条武器信息。 */
@BotPluginCommand(
        code = WarframeCommandCodes.PERRIN_SEQUENCE,
        matchType = MatchType.EXACT,
        name = "佩兰数列",
        gameCode = "warframe",
        order = 68)
public final class PerrinSequencePlugin extends DataSourceCommandPlugin {

    public PerrinSequencePlugin() {
    }
}
