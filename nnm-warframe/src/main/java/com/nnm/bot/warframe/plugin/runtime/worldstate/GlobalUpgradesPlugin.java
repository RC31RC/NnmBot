package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 全局增益命令。 */
@BotPluginCommand(code = WarframeCommandCodes.GLOBAL_UPGRADES, name = "全局增益",
        gameCode = "warframe", order = 65,
        allowRelatedAction = true)
// 官方快照 globalUpgrades 段；升级类型和运算类型由 WorldStateDTO 代码簿解析，当前不新增业务表。
public class GlobalUpgradesPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造全局升级插件，并保存初始化参数。
     */
    public GlobalUpgradesPlugin() {
        super(WarframeWorldStateProjector::globalUpgrades);
    }
}
