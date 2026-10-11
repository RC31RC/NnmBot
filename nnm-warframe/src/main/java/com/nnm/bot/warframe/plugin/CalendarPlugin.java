package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.CALENDAR, name = "实时日历",
        gameCode = "warframe", order = 86)
// 官方 KnownCalendarSeasons；wf_community_dataset_state_translation 译任务说明，
// wf_community_worldstate_language/物品表译奖励；calendar-1999 仍保留静态资料查询。
public class CalendarPlugin extends AbstractWorldStateCommandPlugin {
    public CalendarPlugin() { super(WarframeCommandData::calendar); }
}
