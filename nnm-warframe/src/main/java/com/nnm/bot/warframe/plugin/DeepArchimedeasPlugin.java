package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 深层科研命令。 */
@BotPluginCommand(code = WarframeCommandCodes.DEEP_ARCHIMEDEAS, name = "深层科研", gameCode = "warframe", order = 59)
// 数据源：官方 WorldState.Conquests，按 CT_LAB 过滤；当前不落业务表，原始科研条件保留在 JVM 快照。
// WorldState 代码簿/补充数据用于解析节点、任务类型和未知术语；参考 warframe-worldstate-parser、warframe-info-api。
public class DeepArchimedeasPlugin extends AbstractWorldStateCommandPlugin {
    public DeepArchimedeasPlugin() {
        super(state -> WarframeCommandData.archimedeas(state, "深层科研",
                conquest -> "CT_LAB".equalsIgnoreCase(normalize(conquest.typeKey()))));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.replaceAll("\\s+", "");
    }
}
