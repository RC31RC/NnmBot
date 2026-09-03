package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

import java.util.Locale;

/** 英择谛赏金命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.ENTRATI, name = "英择谛", gameCode = "warframe", order = 54)
public class EntratiPlugin extends AbstractWorldStateCommandPlugin {

    public EntratiPlugin() {
        super(
                state -> WarframeCommandData.syndicate(state, "英择谛",
                        value -> contains(value, "entrati", "deimos", "英择谛")));
    }

    private static boolean contains(String value, String... candidates) {
        String normalized = ObjectUtil.isNull(value) ? "" : value.toLowerCase(Locale.ROOT);
        return java.util.Arrays.stream(candidates)
                .anyMatch(candidate -> normalized.contains(candidate.toLowerCase(Locale.ROOT)));
    }
}
