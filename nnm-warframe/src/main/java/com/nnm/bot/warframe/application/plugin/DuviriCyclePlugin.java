package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 双衍王境轮换命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.DUVIRI_CYCLE, name = "双衍王境", gameCode = "warframe", order = 56)
public class DuviriCyclePlugin extends AbstractWorldStateCommandPlugin {

    public DuviriCyclePlugin() {
        super(
                state -> WarframeCommandData.cycles(state, "双衍王境"));
    }
}
