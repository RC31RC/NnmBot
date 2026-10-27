package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 九重天裂隙命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.VOID_STORMS,
        name = "九重天裂隙",
        gameCode = "warframe",
        order = 49,
        allowRelatedAction = true)
// 无 WorldStateDTO 原始表；读取官方快照的 activeMissions 九重天裂隙，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class VoidStormsPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造虚空风暴插件，并保存初始化参数。
     */
    public VoidStormsPlugin() {
        super(state -> WarframeWorldStateProjector.fissures(
                        state, mission -> mission.voidStorm(), "九重天裂隙"));
    }
}
