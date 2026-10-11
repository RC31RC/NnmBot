package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.model.CommandInvocation;

import com.nnm.bot.core.plugin.model.CommandResult;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询银色遗物奖励推荐。 */
@BotPluginCommand(
        code = WarframeCommandCodes.SILVER_TRASH,
        matchType = MatchType.EXACT,
        name = "银垃圾",
        gameCode = "warframe",
        order = 70)
// 查询 wf_community_relic_reward 的 Uncommon 奖励；遗物及奖励表用途见 AbstractRelicPlugin。
public final class SilverTrashPlugin extends AbstractRelicPlugin {

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return trash("Uncommon", argument(invocation));
    }
}
