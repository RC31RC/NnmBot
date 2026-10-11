package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 虚空商人行踪和商品命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.VOID_TRADER, name = "奸商", gameCode = "warframe", order = 43)
// 无 WorldState 原始表；读取官方快照的 voidTraders 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class VoidTraderPlugin extends AbstractWorldStateCommandPlugin {

    public VoidTraderPlugin() {
        super(WarframeCommandData::voidTrader);
    }
}
