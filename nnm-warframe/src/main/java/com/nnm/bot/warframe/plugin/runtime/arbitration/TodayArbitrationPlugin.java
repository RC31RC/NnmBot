package com.nnm.bot.warframe.plugin.runtime.arbitration;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;

@BotPluginCommand(code = WarframeCommandCodes.TODAY_ARBITRATION, name = "今日仲裁",
        gameCode = "warframe", order = 46,
        allowRelatedAction = true)
// JVM 内存快照 按配置时区自然日（默认上海）查询已缓存的过去/未来任务；
// 人工精华规则只补节点效率，不负责预测任务。
public class TodayArbitrationPlugin extends AbstractArbitrationCommandPlugin {
    /**
     * 构造今日仲裁插件，并保存初始化参数。
     */
    public TodayArbitrationPlugin() { super(QueryType.TODAY); }
}
