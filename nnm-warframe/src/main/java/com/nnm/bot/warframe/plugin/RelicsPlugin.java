package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.model.CommandInvocation;

import com.nnm.bot.core.plugin.model.CommandResult;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询遗物及其奖励。 */
@BotPluginCommand(
        code = WarframeCommandCodes.RELICS,
        matchType = MatchType.PREFIX,
        name = "遗物",
        gameCode = "warframe",
        order = 71)
// 查询 wf_community_relic 及其 wf_community_relic_reward 关联奖励；表用途见 AbstractRelicPlugin。
public final class RelicsPlugin extends AbstractRelicPlugin {

    @Override
    public CommandResult handle(
            CommandInvocation invocation) {
        return relics(argument(invocation));
    }
}
