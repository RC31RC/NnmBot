package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.CIRCUIT_HARD, name = "灵化轮换",
        gameCode = "warframe", order = 85,
        allowRelatedAction = true)
// 官方 EndlessXpSchedule[0].CategoryChoices 的 EXC_HARD；复用物品引用表译灵化武器。
public class CircuitHardPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造回环钢铁插件，并保存初始化参数。
     */
    public CircuitHardPlugin() { super(state -> WarframeWorldStateProjector.circuit(state, true)); }
}
