package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 开放世界周期命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.CYCLES, name = "平原", gameCode = "warframe", order = 52)
// 无周期表；地球/山谷本地算法，希图斯赏金 Expiry 推导平原/魔胎；有效上游周期优先。
public class CyclesPlugin extends AbstractWorldStateCommandPlugin {

    public CyclesPlugin() {
        super(WarframeCommandData::cycles);
    }
}
