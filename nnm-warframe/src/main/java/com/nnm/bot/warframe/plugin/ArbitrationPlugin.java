package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.model.ArbitrationQuery;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/**
 * 当前仲裁，来自独立第三方排期。
 */
@BotPluginCommand(
        code = WarframeCommandCodes.ARBITRATION, name = "仲裁", gameCode = "warframe", order = 30)
// wf_arbitration_schedule 存接口排期；wf_community_arbitration_essence 补节点效率，不用于生成排期。
public class ArbitrationPlugin extends AbstractArbitrationCommandPlugin {

    public ArbitrationPlugin() {
        super(ArbitrationQuery.CURRENT);
    }
}
