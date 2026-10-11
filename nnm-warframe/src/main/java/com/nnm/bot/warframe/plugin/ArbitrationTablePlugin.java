package com.nnm.bot.warframe.plugin;

import com.nnm.bot.warframe.model.ArbitrationQuery;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 仲裁轮换表命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.ARBITRATION_TABLE, name = "仲裁表", gameCode = "warframe", order = 45)
// wf_arbitration_schedule 存未来排期；wf_community_arbitration_essence 补节点效率。
public class ArbitrationTablePlugin extends AbstractArbitrationCommandPlugin {

    public ArbitrationTablePlugin() {
        super(ArbitrationQuery.UPCOMING);
    }
}
