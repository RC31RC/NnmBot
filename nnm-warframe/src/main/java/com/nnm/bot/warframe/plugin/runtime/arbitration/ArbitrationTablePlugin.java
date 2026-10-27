package com.nnm.bot.warframe.plugin.runtime.arbitration;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;

/** 仲裁轮换表命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.ARBITRATION_TABLE, name = "仲裁表", gameCode = "warframe", order = 45,
        allowRelatedAction = true)
// 动态排期来自专用运行时通道；人工精华规则只补充节点效率。
public class ArbitrationTablePlugin extends AbstractArbitrationCommandPlugin {

    /**
     * 构造仲裁表插件，并保存初始化参数。
     */
    public ArbitrationTablePlugin() {
        super(QueryType.UPCOMING);
    }
}
