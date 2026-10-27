package com.nnm.bot.warframe.plugin.runtime.arbitration;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;

@BotPluginCommand(code = WarframeCommandCodes.EFFICIENT_ARBITRATION, name = "高效仲裁",
        gameCode = "warframe", order = 47,
        allowRelatedAction = true)
// JVM 内存快照 读取未来排期，按 NyxBot 任务/星球/阵营规则筛选；
// 人工精华规则只补节点效率，不负责预测任务。
public class EfficientArbitrationPlugin extends AbstractArbitrationCommandPlugin {
    /**
     * 构造高效仲裁插件，并保存初始化参数。
     */
    public EfficientArbitrationPlugin() { super(QueryType.EFFICIENT); }
}
