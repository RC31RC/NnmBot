package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.CONSTRUCTION_PROGRESS, name = "舰队建造",
        gameCode = "warframe", order = 80,
        allowRelatedAction = true)
// 官方 ProjectPct；参考 parser/ConstructionProgress，无静态业务表。
public class ConstructionProgressPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造建设进度插件，并保存初始化参数。
     */
    public ConstructionProgressPlugin() { super(WarframeWorldStateProjector::constructionProgress); }
}
