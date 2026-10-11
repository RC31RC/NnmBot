package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.CONSTRUCTION_PROGRESS, name = "舰队建造",
        gameCode = "warframe", order = 80)
// 官方 ProjectPct；参考 parser/ConstructionProgress，无静态业务表。
public class ConstructionProgressPlugin extends AbstractWorldStateCommandPlugin {
    public ConstructionProgressPlugin() { super(WarframeCommandData::constructionProgress); }
}
