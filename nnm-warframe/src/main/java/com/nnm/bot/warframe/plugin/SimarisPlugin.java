package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.SIMARIS, name = "西玛里斯",
        gameCode = "warframe", order = 81)
// 官方 LibraryInfo；wf_community_worldstate_language 译公共研究目标，非玩家个人每日目标。
public class SimarisPlugin extends AbstractWorldStateCommandPlugin {
    public SimarisPlugin() { super(WarframeCommandData::simaris); }
}
