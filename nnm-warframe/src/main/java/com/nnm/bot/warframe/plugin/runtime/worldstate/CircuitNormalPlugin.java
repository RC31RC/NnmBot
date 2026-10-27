package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.CIRCUIT_NORMAL, name = "普通回环",
        gameCode = "warframe", order = 84,
        allowRelatedAction = true)
// 官方 EndlessXpSchedule[0].CategoryChoices；翻译/物品表译名称，不读取双衍情绪倒计时。
public class CircuitNormalPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造回环普通插件，并保存初始化参数。
     */
    public CircuitNormalPlugin() { super(state -> WarframeWorldStateProjector.circuit(state, false)); }
}
