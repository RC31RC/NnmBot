package com.nnm.bot.warframe.plugin.persistent.official.items;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;

@BotPluginCommand(code = WarframeCommandCodes.BOOSTER_PACKS, matchType = MatchType.PREFIX,
        name = "随机礼包", gameCode = "warframe", order = 93)
public final class BoosterPacksPlugin extends AbstractBundlePlugin {
    /**
     * 构造增益道具礼包插件，并保存初始化参数。
     */
    public BoosterPacksPlugin() {
        super(Category.BOOSTER_PACK);
    }
}
