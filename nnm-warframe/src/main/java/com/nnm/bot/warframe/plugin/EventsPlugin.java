package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 官方活动命令。 */
@BotPluginCommand(code = WarframeCommandCodes.EVENTS, name = "活动",
        gameCode = "warframe", order = 64)
// 官方快照 events 段；奖励和节点引用由 WorldState 代码簿/社区数据解析，当前不新增业务表。
public class EventsPlugin extends AbstractWorldStateCommandPlugin {
    public EventsPlugin() {
        super(WarframeCommandData::events);
    }
}
