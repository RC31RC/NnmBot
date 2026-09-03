package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

import java.util.Locale;

/** 希图斯赏金命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.OSTRONS, name = "希图斯", gameCode = "warframe", order = 53)
public class OstronsPlugin extends AbstractWorldStateCommandPlugin {

    public OstronsPlugin() {
        super(state -> WarframeCommandData.syndicate(state, "希图斯",
                        value -> contains(value, "ostrons", "cetus", "希图斯")));
    }

    private static boolean contains(String value, String... candidates) {
        String normalized = ObjectUtil.isNull(value) ? "" : value.toLowerCase(Locale.ROOT);
        return java.util.Arrays.stream(candidates)
                .anyMatch(candidate -> normalized.contains(candidate.toLowerCase(Locale.ROOT)));
    }
}
