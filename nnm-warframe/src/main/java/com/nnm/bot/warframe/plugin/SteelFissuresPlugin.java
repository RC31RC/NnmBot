package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 钢铁路径裂隙命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.STEEL_FISSURES,
        name = "钢铁裂隙",
        gameCode = "warframe",
        order = 50)
// 无 WorldState 原始表；读取官方快照的 activeMissions 钢铁裂隙，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class SteelFissuresPlugin extends AbstractWorldStateCommandPlugin {

    public SteelFissuresPlugin() {
        super(state -> WarframeCommandData.fissures(
                        state, mission -> mission.hard() && !mission.voidStorm(), "钢铁裂隙"));
    }
}
