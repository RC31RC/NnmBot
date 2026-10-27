package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.TIMESTAMP, name = "世界时间",
        gameCode = "warframe", order = 88,
        allowRelatedAction = true)
// 官方 Time/timestamp；仅显示当前快照时间，无静态业务表。
public class WorldTimestampPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造世界时间戳插件，并保存初始化参数。
     */
    public WorldTimestampPlugin() { super(WarframeWorldStateProjector::timestamp); }
}
