package com.nnm.bot.warframe.plugin.runtime.arbitration;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.constant.ArbitrationConstants.QueryType;

/**
 * 当前仲裁，来自独立第三方排期。
 */
@BotPluginCommand(
        code = WarframeCommandCodes.ARBITRATION, name = "仲裁", gameCode = "warframe", order = 30,
        allowRelatedAction = true)
// 动态排期来自专用运行时通道；人工精华规则只补充节点效率，不生成排期。
public class ArbitrationPlugin extends AbstractArbitrationCommandPlugin {

    /**
     * 构造仲裁插件，并保存初始化参数。
     */
    public ArbitrationPlugin() {
        super(QueryType.CURRENT);
    }
}
