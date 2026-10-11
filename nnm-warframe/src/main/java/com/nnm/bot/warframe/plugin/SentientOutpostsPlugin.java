package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.SENTIENT_OUTPOSTS, name = "先遣舰",
        gameCode = "warframe", order = 82)
// 官方 Tmp.sfn；wf_community_worldstate_node 译 CrewBattleNode，参考 Tmp/SentientOutpost。
public class SentientOutpostsPlugin extends AbstractWorldStateCommandPlugin {
    public SentientOutpostsPlugin() { super(WarframeCommandData::sentientOutposts); }
}
