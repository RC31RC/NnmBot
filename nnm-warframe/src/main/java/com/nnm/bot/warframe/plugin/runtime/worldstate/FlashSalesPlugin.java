package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.FLASH_SALES, name = "促销",
        gameCode = "warframe", order = 66,
        allowRelatedAction = true)
// 官方快照 FlashSales；对应官方商品主表和 WorldStateDTO 代码簿解析商品引用。
public class FlashSalesPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造限时促销插件，并保存初始化参数。
     */
    public FlashSalesPlugin() {
        super(WarframeWorldStateProjector::flashSales);
    }
}
