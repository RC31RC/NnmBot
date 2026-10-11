package com.nnm.bot.warframe.plugin;

import java.util.Arrays;
import java.util.Locale;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 扎里曼坚守者赏金命令。 */
@BotPluginCommand(code = WarframeCommandCodes.HOLDFASTS, name = "扎里曼赏金",
        gameCode = "warframe", order = 62)
// 官方快照 syndicateMissions；过滤 The Holdfasts 集团。任务文本/奖励引用由 WorldState 代码簿及社区数据补全。
public class HoldfastsPlugin extends AbstractWorldStateCommandPlugin {
    public HoldfastsPlugin() {
        super(state -> WarframeCommandData.syndicate(state, "扎里曼赏金",
                value -> contains(value, "holdfast", "坚守者", "zariman")));
    }

    private static boolean contains(String value, String... candidates) {
        String normalized = value == null ? "" : value.toLowerCase(Locale.ROOT);
        return Arrays.stream(candidates)
                .anyMatch(candidate -> normalized.contains(candidate.toLowerCase(Locale.ROOT)));
    }
}
