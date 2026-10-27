package com.nnm.bot.warframe.plugin.persistent.official.items;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;

@BotPluginCommand(code = WarframeCommandCodes.BUNDLES, matchType = MatchType.PREFIX,
        name = "礼包内容", gameCode = "warframe", order = 92)
public final class BundlesPlugin extends AbstractBundlePlugin {
    /**
     * 构造礼包插件，并保存初始化参数。
     */
    public BundlesPlugin() {
        super(Category.BUNDLE);
    }
}
