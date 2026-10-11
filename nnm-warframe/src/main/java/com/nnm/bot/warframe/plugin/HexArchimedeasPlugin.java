package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 时光科研命令。 */
@BotPluginCommand(code = WarframeCommandCodes.HEX_ARCHIMEDEAS, name = "时光科研", gameCode = "warframe", order = 60)
// 数据源：官方 WorldState.Conquests，按 CT_HEX 过滤；当前不落业务表，原始科研条件保留在 JVM 快照。
// WorldState 代码簿/补充数据用于解析节点、任务类型和未知术语；参考 warframe-worldstate-parser、warframe-info-api。
public class HexArchimedeasPlugin extends AbstractWorldStateCommandPlugin {
    public HexArchimedeasPlugin() {
        super(state -> WarframeCommandData.archimedeas(state, "时光科研",
                conquest -> "CT_HEX".equalsIgnoreCase(normalize(conquest.typeKey()))));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.replaceAll("\\s+", "");
    }
}
