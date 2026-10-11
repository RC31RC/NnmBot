package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 当前电波任务命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.NIGHTWAVE, name = "电波", gameCode = "warframe", order = 57)
// 无 WorldState 原始表；读取官方快照的 nightwave 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class NightwavePlugin extends AbstractWorldStateCommandPlugin {

    public NightwavePlugin() {
        super(WarframeCommandData::nightwave);
    }
}
