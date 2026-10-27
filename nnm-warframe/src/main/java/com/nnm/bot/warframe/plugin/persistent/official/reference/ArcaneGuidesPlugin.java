package com.nnm.bot.warframe.plugin.persistent.official.reference;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;

@BotPluginCommand(code = WarframeCommandCodes.ARCANE_GUIDES, matchType = MatchType.PREFIX,
        name = "奥术攻略", gameCode = "warframe", order = 125)
// WorldStateDTO arcanes.json 是历史攻略；当前奥术属性由 arcanes 命令独立提供。
public final class ArcaneGuidesPlugin extends AbstractDefinitionPlugin {
    /**
     * 取得当前独立数据文件的领域标识。
     *
     * @return 当前独立 JSON 的领域标识。
     */
    @Override
    protected Category domain() {
        return Category.RULE_ARCANE_GUIDE;
    }
}
