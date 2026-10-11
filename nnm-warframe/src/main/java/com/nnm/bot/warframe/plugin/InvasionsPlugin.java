package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 当前入侵命令。 */
@BotPluginCommand(code = WarframeCommandCodes.INVASIONS, name = "入侵", gameCode = "warframe", order = 47)
// 无 WorldState 原始表；读取官方快照的 invasions 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class InvasionsPlugin extends AbstractWorldStateCommandPlugin {

    public InvasionsPlugin() {
        super(WarframeCommandData::invasions);
    }
}
