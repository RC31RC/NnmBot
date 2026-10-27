package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.CALENDAR, name = "实时日历",
        gameCode = "warframe", order = 86,
        allowRelatedAction = true)
public class CalendarPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造日历插件，并保存初始化参数。
     */
    public CalendarPlugin() { super(WarframeWorldStateProjector::calendar); }
}
