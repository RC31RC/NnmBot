package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 当前入侵命令。 */
@BotPluginCommand(code = WarframeCommandCodes.INVASIONS, name = "入侵", gameCode = "warframe", order = 47,
        allowRelatedAction = true)
// 无 WorldStateDTO 原始表；读取官方快照的 invasions 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class InvasionsPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造入侵插件，并保存初始化参数。
     */
    public InvasionsPlugin() {
        super(WarframeWorldStateProjector::invasions);
    }
}
