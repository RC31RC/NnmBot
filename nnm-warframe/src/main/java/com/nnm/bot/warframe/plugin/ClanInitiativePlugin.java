package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.CLAN_INITIATIVE, name = "氏族周任务",
        gameCode = "warframe", order = 83)
// 官方 WeeklyVaultBonusRewards；wf_community_worldstate_language 与物品引用表译区域和奖励。
public class ClanInitiativePlugin extends AbstractWorldStateCommandPlugin {
    public ClanInitiativePlugin() { super(WarframeCommandData::clanInitiatives); }
}
