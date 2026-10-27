package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 钢铁路径奖励命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.STEEL_PATH, name = "钢铁奖励", gameCode = "warframe", order = 51,
        allowRelatedAction = true)
// 无 WorldStateDTO 原始表；读取官方快照的 steelPath 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class SteelPathPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造钢铁路径插件，并保存初始化参数。
     */
    public SteelPathPlugin() {
        super(WarframeWorldStateProjector::steelPath);
    }
}
