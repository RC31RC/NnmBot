package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.CIRCUIT_HARD, name = "灵化轮换",
        gameCode = "warframe", order = 85)
// 官方 EndlessXpSchedule[0].CategoryChoices 的 EXC_HARD；复用物品引用表译灵化武器。
public class CircuitHardPlugin extends AbstractWorldStateCommandPlugin {
    public CircuitHardPlugin() { super(state -> WarframeCommandData.circuit(state, true)); }
}
