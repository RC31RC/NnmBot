package com.nnm.bot.warframe.plugin;

import java.util.Arrays;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

import java.util.Locale;

/** 希图斯赏金命令。 */
@BotPluginCommand(code = WarframeCommandCodes.OSTRONS, name = "希图斯", gameCode = "warframe", order = 53)
// 官方快照的 syndicateMissions 段；wf_community_dataset_state_translation 译赏金文本，
// wf_community_dataset_reward_pool 补缺失奖励池，其他静态引用表用途见 AbstractWorldStateCommandPlugin。
public class OstronsPlugin extends AbstractWorldStateCommandPlugin {

    public OstronsPlugin() {
        super(state -> WarframeCommandData.syndicate(state, "希图斯",
                        value -> contains(value, "ostrons", "cetus", "希图斯")));
    }

    private static boolean contains(String value, String... candidates) {
        String normalized = ObjectUtil.isNull(value) ? "" : value.toLowerCase(Locale.ROOT);
        return Arrays.stream(candidates)
                .anyMatch(candidate -> normalized.contains(candidate.toLowerCase(Locale.ROOT)));
    }
}
