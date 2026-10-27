package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 当前突击命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.SORTIES, name = "突击", gameCode = "warframe", order = 41,
        allowRelatedAction = true)
// 无 WorldStateDTO 原始表；读取官方快照的 sorties 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class SortiesPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造突击插件，并保存初始化参数。
     */
    public SortiesPlugin() {
        super(state -> WarframeWorldStateProjector.sorties(state, false));
    }
}
