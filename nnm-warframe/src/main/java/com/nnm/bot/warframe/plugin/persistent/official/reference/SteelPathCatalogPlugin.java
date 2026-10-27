package com.nnm.bot.warframe.plugin.persistent.official.reference;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;

@BotPluginCommand(code = WarframeCommandCodes.STEEL_PATH_CATALOG, matchType = MatchType.PREFIX,
        name = "钢铁奖励目录", gameCode = "warframe", order = 122)
// WorldStateDTO steelPath.json 静态分区和已采纳商品，不替代当前库存命令。
public final class SteelPathCatalogPlugin extends AbstractDefinitionPlugin {
    /**
     * 取得当前独立数据文件的领域标识。
     *
     * @return 当前独立 JSON 的领域标识。
     */
    @Override
    protected Category domain() {
        return Category.RULE_STEEL_PATH;
    }
}
