package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.SYNDICATE_MISSIONS, name = "集团任务",
        gameCode = "warframe", order = 68,
        allowRelatedAction = true)
public class SyndicateMissionsPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造集团任务插件，并保存初始化参数。
     */
    public SyndicateMissionsPlugin() {
        super(state -> WarframeWorldStateProjector.syndicate(state, "集团任务", value -> true));
    }
}
