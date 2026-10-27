package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.VAULT_TRADER, name = "宝库商人",
        gameCode = "warframe", order = 87,
        allowRelatedAction = true)
public class VaultTraderPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造宝库商人插件，并保存初始化参数。
     */
    public VaultTraderPlugin() { super(WarframeWorldStateProjector::vaultTrader); }
}
