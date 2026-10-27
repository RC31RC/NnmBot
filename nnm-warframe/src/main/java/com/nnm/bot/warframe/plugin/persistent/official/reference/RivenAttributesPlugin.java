package com.nnm.bot.warframe.plugin.persistent.official.reference;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;

@BotPluginCommand(code = WarframeCommandCodes.RIVEN_ATTRIBUTES, matchType = MatchType.PREFIX,
        name = "历史紫卡属性编码", gameCode = "warframe", order = 111)
public final class RivenAttributesPlugin extends AbstractDefinitionPlugin {
    /**
     * 取得当前独立数据文件的领域标识。
     *
     * @return 当前独立 JSON 的领域标识。
     */
    @Override
    protected Category domain() {
        return Category.REFERENCE_RIVEN_ATTRIBUTE;
    }
}
