package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 当前执刑官猎杀命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.ARCHON_HUNTS, name = "执刑官猎杀", gameCode = "warframe", order = 42,
        allowRelatedAction = true)
// 无 WorldStateDTO 原始表；读取官方快照的 archonHunt 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class ArchonHuntPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造执刑官狩猎插件，并保存初始化参数。
     */
    public ArchonHuntPlugin() {
        super(
                state -> WarframeWorldStateProjector.sorties(state, true));
    }
}
