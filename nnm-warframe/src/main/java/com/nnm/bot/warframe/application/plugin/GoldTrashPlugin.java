package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询金色遗物奖励推荐。 */
@BotPluginCommand(
        code = WarframeCommandCodes.GOLD_TRASH, name = "金垃圾", gameCode = "warframe", order = 69)
public final class GoldTrashPlugin extends DataSourceCommandPlugin {

    public GoldTrashPlugin() {
    }
}
