package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 钢铁路径裂隙命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.STEEL_FISSURES,
        name = "钢铁裂隙",
        gameCode = "warframe",
        order = 50,
        allowRelatedAction = true)
// 无 WorldStateDTO 原始表；读取官方快照的 activeMissions 钢铁裂隙，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class SteelFissuresPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造钢铁裂隙插件，并保存初始化参数。
     */
    public SteelFissuresPlugin() {
        super(state -> WarframeWorldStateProjector.fissures(
                        state, mission -> mission.hard() && !mission.voidStorm(), "钢铁裂隙"));
    }
}
