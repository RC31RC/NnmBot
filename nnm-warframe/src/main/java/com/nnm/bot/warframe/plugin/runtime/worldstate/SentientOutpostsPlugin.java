package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.SENTIENT_OUTPOSTS, name = "先遣舰",
        gameCode = "warframe", order = 82,
        allowRelatedAction = true)
public class SentientOutpostsPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造Sentient前哨站插件，并保存初始化参数。
     */
    public SentientOutpostsPlugin() { super(WarframeWorldStateProjector::sentientOutposts); }
}
