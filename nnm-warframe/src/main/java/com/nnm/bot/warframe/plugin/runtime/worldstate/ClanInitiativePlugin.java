package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.CLAN_INITIATIVE, name = "氏族周任务",
        gameCode = "warframe", order = 83,
        allowRelatedAction = true)
public class ClanInitiativePlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造氏族倡议插件，并保存初始化参数。
     */
    public ClanInitiativePlugin() { super(WarframeWorldStateProjector::clanInitiatives); }
}
