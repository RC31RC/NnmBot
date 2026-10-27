package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 官方活动命令。 */
@BotPluginCommand(code = WarframeCommandCodes.EVENTS, name = "活动",
        gameCode = "warframe", order = 64,
        allowRelatedAction = true)
// 官方快照 events 段；奖励和节点引用由已采纳 WorldStateDTO 代码簿解析，未命中时保留未知引用。
public class EventsPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造事件插件，并保存初始化参数。
     */
    public EventsPlugin() {
        super(WarframeWorldStateProjector::events);
    }
}
