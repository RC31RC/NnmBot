package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** 科研命令。 */
@BotPluginCommand(code = WarframeCommandCodes.ARCHIMEDEAS, name = "科研", gameCode = "warframe", order = 58,
        allowRelatedAction = true)
// 数据源：官方 WorldStateDTO.Conquests；当前不落业务表，原始科研条件保留在 JVM 快照。
// WorldStateDTO 代码簿/补充数据用于解析节点、任务类型和未知术语；参考 warframe-worldstate-parser、warframe-info-api。
public class ArchimedeasPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造科研插件，并保存初始化参数。
     */
    public ArchimedeasPlugin() {
        super(state -> WarframeWorldStateProjector.archimedeas(state, "科研", conquest -> true));
    }
}
