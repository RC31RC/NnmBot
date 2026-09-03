package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

import java.util.Locale;

/** 索拉里斯联合赏金命令。 */
@BotPluginCommand(
        code = WarframeCommandCodes.SOLARIS, name = "索拉里斯", gameCode = "warframe", order = 55)
public class SolarisPlugin extends AbstractWorldStateCommandPlugin {

    public SolarisPlugin() {
        super(state -> WarframeCommandData.syndicate(state, "索拉里斯",
                        value -> contains(value, "solaris", "vallis", "索拉里斯")));
    }

    private static boolean contains(String value, String... candidates) {
        String normalized = ObjectUtil.isNull(value) ? "" : value.toLowerCase(Locale.ROOT);
        return java.util.Arrays.stream(candidates)
                .anyMatch(candidate -> normalized.contains(candidate.toLowerCase(Locale.ROOT)));
    }
}
