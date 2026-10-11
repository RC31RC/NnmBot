package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.model.ArbitrationQuery;

@BotPluginCommand(code = WarframeCommandCodes.EFFICIENT_ARBITRATION, name = "高效仲裁",
        gameCode = "warframe", order = 47)
// wf_arbitration_schedule 读取未来排期，按 NyxBot 任务/星球/阵营规则筛选；
// wf_community_arbitration_essence 只补节点精华效率，不负责预测任务。
public class EfficientArbitrationPlugin extends AbstractArbitrationCommandPlugin {
    public EfficientArbitrationPlugin() { super(ArbitrationQuery.EFFICIENT); }
}
