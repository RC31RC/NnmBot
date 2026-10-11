package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 全局增益命令。 */
@BotPluginCommand(code = WarframeCommandCodes.GLOBAL_UPGRADES, name = "全局增益",
        gameCode = "warframe", order = 65)
// 官方快照 globalUpgrades 段；升级类型和运算类型由 WorldState 代码簿解析，当前不新增业务表。
public class GlobalUpgradesPlugin extends AbstractWorldStateCommandPlugin {
    public GlobalUpgradesPlugin() {
        super(WarframeCommandData::globalUpgrades);
    }
}
