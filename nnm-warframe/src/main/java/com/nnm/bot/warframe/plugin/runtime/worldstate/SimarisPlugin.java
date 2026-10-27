package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.SIMARIS, name = "西玛里斯",
        gameCode = "warframe", order = 81,
        allowRelatedAction = true)
public class SimarisPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造Simaris插件，并保存初始化参数。
     */
    public SimarisPlugin() { super(WarframeWorldStateProjector::simaris); }
}
