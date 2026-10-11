package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 深层下降命令。 */
@BotPluginCommand(code = WarframeCommandCodes.DESCENTS, name = "深层下降",
        gameCode = "warframe", order = 61)
// 数据源：官方 WorldState.Descents；当前轮换保存在 JVM 快照，不使用业务表。
// Type/Challenge/Level/Specs/Auras 保留原始键，未知术语由管理端原始快照和代码簿继续审计。
// 参考：warframe-worldstate-parser 的 Descendia/RawDescent。
public class DescentsPlugin extends AbstractWorldStateCommandPlugin {
    public DescentsPlugin() {
        super(WarframeCommandData::descents);
    }
}
