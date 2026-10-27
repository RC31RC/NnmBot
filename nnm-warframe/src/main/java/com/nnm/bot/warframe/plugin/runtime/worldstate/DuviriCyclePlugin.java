package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 双衍王境轮换命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.DUVIRI_CYCLE, name = "双衍王境", gameCode = "warframe", order = 56,
        allowRelatedAction = true)
// 无 WorldStateDTO 原始表；仅在官方快照实际含 duviriCycle 段时展示，当前官方 worldState.php 样本未提供该段。
public class DuviriCyclePlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造双衍王境周期插件，并保存初始化参数。
     */
    public DuviriCyclePlugin() {
        super(
                state -> WarframeWorldStateProjector.cycles(state, "双衍王境"));
    }
}
