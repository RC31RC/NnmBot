package com.nnm.bot.warframe.plugin;

import java.util.Arrays;
import java.util.Locale;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 1999 霍瓦尼亚六人组赏金命令。 */
@BotPluginCommand(code = WarframeCommandCodes.HEX_BOUNTIES, name = "1999赏金",
        gameCode = "warframe", order = 63)
// 官方快照 syndicateMissions；过滤 The Hex 集团。任务文本/奖励引用由 WorldState 代码簿及社区数据补全。
public class HexBountiesPlugin extends AbstractWorldStateCommandPlugin {
    public HexBountiesPlugin() {
        super(state -> WarframeCommandData.syndicate(state, "1999赏金",
                value -> contains(value, "the hex", "hex", "六人组", "霍瓦尼亚")));
    }

    private static boolean contains(String value, String... candidates) {
        String normalized = value == null ? "" : value.toLowerCase(Locale.ROOT);
        return Arrays.stream(candidates)
                .anyMatch(candidate -> normalized.contains(candidate.toLowerCase(Locale.ROOT)));
    }
}
