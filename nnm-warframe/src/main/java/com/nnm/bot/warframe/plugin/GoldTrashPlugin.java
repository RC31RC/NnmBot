package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.model.CommandInvocation;

import com.nnm.bot.core.plugin.model.CommandResult;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询金色遗物奖励推荐。 */
@BotPluginCommand(
        code = WarframeCommandCodes.GOLD_TRASH, name = "金垃圾", gameCode = "warframe", order = 69)
// 查询 wf_community_relic_reward 的 Rare 奖励；遗物及奖励表用途见 AbstractRelicPlugin。
public final class GoldTrashPlugin extends AbstractRelicPlugin {

    @Override
    public CommandResult handle(
            CommandInvocation invocation) {
        return trash("Rare", argument(invocation));
    }
}
