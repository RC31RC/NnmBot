package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.TIMESTAMP, name = "世界时间",
        gameCode = "warframe", order = 88)
// 官方 Time/timestamp；仅显示当前快照时间，无静态业务表。
public class WorldTimestampPlugin extends AbstractWorldStateCommandPlugin {
    public WorldTimestampPlugin() { super(WarframeCommandData::timestamp); }
}
