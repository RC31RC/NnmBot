package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.CIRCUIT_NORMAL, name = "普通回环",
        gameCode = "warframe", order = 84)
// 官方 EndlessXpSchedule[0].CategoryChoices；翻译/物品表译名称，不读取双衍情绪倒计时。
public class CircuitNormalPlugin extends AbstractWorldStateCommandPlugin {
    public CircuitNormalPlugin() { super(state -> WarframeCommandData.circuit(state, false)); }
}
