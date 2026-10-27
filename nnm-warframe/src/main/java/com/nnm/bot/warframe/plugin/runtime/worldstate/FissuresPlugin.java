package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 普通虚空裂隙命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.FISSURES, name = "裂隙", gameCode = "warframe", order = 48,
        allowRelatedAction = true)
// 无 WorldStateDTO 原始表；读取官方快照的 activeMissions 裂隙，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class FissuresPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造裂隙插件，并保存初始化参数。
     */
    public FissuresPlugin() {
        super(
                state -> WarframeWorldStateProjector.fissures(state,
                        mission -> !mission.hard() && !mission.voidStorm(), "裂隙"));
    }
}
