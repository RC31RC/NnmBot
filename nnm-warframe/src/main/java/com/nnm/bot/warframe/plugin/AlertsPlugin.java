package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 当前警报命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.ALERTS, name = "警报", gameCode = "warframe", order = 40)
// 无 WorldState 原始表；读取官方快照的 alerts 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class AlertsPlugin extends AbstractWorldStateCommandPlugin {

    public AlertsPlugin() {
        super(WarframeCommandData::alerts);
    }
}
