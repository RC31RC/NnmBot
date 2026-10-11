package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.VAULT_TRADER, name = "宝库商人",
        gameCode = "warframe", order = 87)
// 官方 PrimeVaultTraders；wf_community_worldstate_node 与物品引用表译地点和库存。
public class VaultTraderPlugin extends AbstractWorldStateCommandPlugin {
    public VaultTraderPlugin() { super(WarframeCommandData::vaultTrader); }
}
