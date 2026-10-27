package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;
import java.util.Arrays;
import java.util.Locale;

/** 1999 霍瓦尼亚六人组赏金命令。 */
@BotPluginCommand(code = WarframeCommandCodes.HEX_BOUNTIES, name = "1999赏金",
        gameCode = "warframe", order = 63,
        allowRelatedAction = true)
// 官方快照 syndicateMissions；过滤 The Hex 集团。任务文本/奖励引用由 WorldStateDTO 代码簿及社区数据补全。
public class HexBountiesPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造六人组赏金插件，并保存初始化参数。
     */
    public HexBountiesPlugin() {
        super(state -> WarframeWorldStateProjector.syndicate(state, "1999赏金",
                value -> contains(value, "the hex", "hex", "六人组", "霍瓦尼亚")));
    }

    /**
     * 判断文本或集合是否包含指定内容。
     *
     * @param value 待校验或规范化的文本。
     * @param candidates 候选。
     * @return 满足文本或集合是否包含指定内容时为真，否则为假。
     */
    private static boolean contains(String value, String... candidates) {
        String normalized = value == null ? "" : value.toLowerCase(Locale.ROOT);
        return Arrays.stream(candidates)
                .anyMatch(candidate -> normalized.contains(candidate.toLowerCase(Locale.ROOT)));
    }
}
