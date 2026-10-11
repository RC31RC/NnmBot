package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 当前突击命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.SORTIES, name = "突击", gameCode = "warframe", order = 41)
// 无 WorldState 原始表；读取官方快照的 sorties 段，静态引用表用途见 AbstractWorldStateCommandPlugin。
public class SortiesPlugin extends AbstractWorldStateCommandPlugin {

    public SortiesPlugin() {
        super(state -> WarframeCommandData.sorties(state, false));
    }
}
