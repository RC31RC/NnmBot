package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 每日特惠命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.DAILY_DEALS, name = "每日特惠", gameCode = "warframe", order = 46)
// 无 WorldState 原始表；读取官方快照的 dailyDeals 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class DailyDealsPlugin extends AbstractWorldStateCommandPlugin {

    public DailyDealsPlugin() {
        super(WarframeCommandData::dailyDeals);
    }
}
