package com.nnm.bot.warframe.plugin.persistent.official.reference;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;

@BotPluginCommand(code = WarframeCommandCodes.VIRTUALS, matchType = MatchType.PREFIX,
        name = "虚拟实体", gameCode = "warframe", order = 98)
public final class VirtualsPlugin extends AbstractDefinitionPlugin {
    /**
     * 取得当前独立数据文件的领域标识。
     *
     * @return 当前独立 JSON 的领域标识。
     */
    @Override
    protected Category domain() {
        return Category.REFERENCE_VIRTUAL;
    }
}
