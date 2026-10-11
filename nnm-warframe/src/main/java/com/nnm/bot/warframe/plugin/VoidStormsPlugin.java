package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 九重天裂隙命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.VOID_STORMS,
        name = "九重天裂隙",
        gameCode = "warframe",
        order = 49)
// 无 WorldState 原始表；读取官方快照的 activeMissions 九重天裂隙，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class VoidStormsPlugin extends AbstractWorldStateCommandPlugin {

    public VoidStormsPlugin() {
        super(state -> WarframeCommandData.fissures(
                        state, mission -> mission.voidStorm(), "九重天裂隙"));
    }
}
