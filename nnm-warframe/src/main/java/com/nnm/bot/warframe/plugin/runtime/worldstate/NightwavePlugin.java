package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 当前电波任务命令。 */
@BotPluginCommand(code = WarframeCommandCodes.NIGHTWAVE)
// 无 WorldStateDTO 原始表；读取官方快照的 nightwave 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class NightwavePlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造夜波插件，并保存初始化参数。
     */
    public NightwavePlugin() {
        super(WarframeWorldStateProjector::nightwave);
    }
}
