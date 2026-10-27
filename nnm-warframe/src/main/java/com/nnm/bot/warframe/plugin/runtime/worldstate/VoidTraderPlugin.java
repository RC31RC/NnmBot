package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 虚空商人行踪和商品命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.VOID_TRADER, name = "奸商", gameCode = "warframe", order = 43,
        allowRelatedAction = true)
// 无 WorldStateDTO 原始表；读取官方快照的 voidTraders 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class VoidTraderPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造虚空商人插件，并保存初始化参数。
     */
    public VoidTraderPlugin() {
        super(WarframeWorldStateProjector::voidTrader);
    }
}
