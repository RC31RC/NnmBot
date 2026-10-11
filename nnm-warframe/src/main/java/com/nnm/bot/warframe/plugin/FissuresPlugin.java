package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 普通虚空裂隙命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.FISSURES, name = "裂隙", gameCode = "warframe", order = 48)
// 无 WorldState 原始表；读取官方快照的 activeMissions 裂隙，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class FissuresPlugin extends AbstractWorldStateCommandPlugin {

    public FissuresPlugin() {
        super(
                state -> WarframeCommandData.fissures(state,
                        mission -> !mission.hard() && !mission.voidStorm(), "裂隙"));
    }
}
