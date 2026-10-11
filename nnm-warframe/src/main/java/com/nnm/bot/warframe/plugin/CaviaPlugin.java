package com.nnm.bot.warframe.plugin;

import java.util.Arrays;
import java.util.Locale;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 科维兽赏金命令。 */
@BotPluginCommand(code = WarframeCommandCodes.CAVIA, name = "科维兽赏金",
        gameCode = "warframe", order = 61)
// 官方快照 syndicateMissions；过滤 Cavia 集团。任务文本/奖励引用由 WorldState 代码簿及社区数据补全。
public class CaviaPlugin extends AbstractWorldStateCommandPlugin {
    public CaviaPlugin() {
        super(state -> WarframeCommandData.syndicate(state, "科维兽赏金",
                value -> contains(value, "cavia", "科维")));
    }

    private static boolean contains(String value, String... candidates) {
        String normalized = value == null ? "" : value.toLowerCase(Locale.ROOT);
        return Arrays.stream(candidates)
                .anyMatch(candidate -> normalized.contains(candidate.toLowerCase(Locale.ROOT)));
    }
}
