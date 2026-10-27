package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 当前警报命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.ALERTS, name = "警报", gameCode = "warframe", order = 40,
        allowRelatedAction = true)
// 无 WorldStateDTO 原始表；读取官方快照的 alerts 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class AlertsPlugin extends AbstractWorldStateCommandPlugin {

    /**
     * 构造警报插件，并保存初始化参数。
     */
    public AlertsPlugin() {
        super(WarframeWorldStateProjector::alerts);
    }
}
