package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 双衍王境轮换命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.DUVIRI_CYCLE, name = "双衍王境", gameCode = "warframe", order = 56)
// 无 WorldState 原始表；仅在官方快照实际含 duviriCycle 段时展示，当前官方 worldState.php 样本未提供该段。
public class DuviriCyclePlugin extends AbstractWorldStateCommandPlugin {

    public DuviriCyclePlugin() {
        super(
                state -> WarframeCommandData.cycles(state, "双衍王境"));
    }
}
