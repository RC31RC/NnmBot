package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 每日特惠命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.DAILY_DEALS, name = "每日特惠", gameCode = "warframe", order = 46,
        allowRelatedAction = true)
// 无 WorldStateDTO 原始表；读取官方快照的 dailyDeals 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class DailyDealsPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造每日特惠插件，并保存初始化参数。
     */
    public DailyDealsPlugin() {
        super(WarframeWorldStateProjector::dailyDeals);
    }
}
