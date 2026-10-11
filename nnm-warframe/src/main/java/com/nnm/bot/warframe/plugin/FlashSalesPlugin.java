package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.FLASH_SALES, name = "促销",
        gameCode = "warframe", order = 66)
// 官方快照 FlashSales；wf_official_export_record 和 WorldState 代码簿解析商品引用。
public class FlashSalesPlugin extends AbstractWorldStateCommandPlugin {
    public FlashSalesPlugin() {
        super(WarframeCommandData::flashSales);
    }
}
