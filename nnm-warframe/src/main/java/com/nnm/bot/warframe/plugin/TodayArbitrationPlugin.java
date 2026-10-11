package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.model.ArbitrationQuery;

@BotPluginCommand(code = WarframeCommandCodes.TODAY_ARBITRATION, name = "今日仲裁",
        gameCode = "warframe", order = 46)
// wf_arbitration_schedule 按配置时区自然日（默认上海）查询已存档的过去/未来任务；
// wf_community_arbitration_essence 只补节点精华效率，不负责预测任务。
public class TodayArbitrationPlugin extends AbstractArbitrationCommandPlugin {
    public TodayArbitrationPlugin() { super(ArbitrationQuery.TODAY); }
}
