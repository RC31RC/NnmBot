package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 科研命令。 */
@BotPluginCommand(code = WarframeCommandCodes.ARCHIMEDEAS, name = "科研", gameCode = "warframe", order = 58)
// 数据源：官方 WorldState.Conquests；当前不落业务表，原始科研条件保留在 JVM 快照。
// WorldState 代码簿/补充数据用于解析节点、任务类型和未知术语；参考 warframe-worldstate-parser、warframe-info-api。
public class ArchimedeasPlugin extends AbstractWorldStateCommandPlugin {
    public ArchimedeasPlugin() {
        super(state -> WarframeCommandData.archimedeas(state, "科研", conquest -> true));
    }
}
