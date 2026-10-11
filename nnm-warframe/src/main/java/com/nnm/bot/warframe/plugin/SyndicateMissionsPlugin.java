package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.SYNDICATE_MISSIONS, name = "集团任务",
        gameCode = "warframe", order = 68)
// 官方快照 SyndicateMissions 全集团视图；wf_community_dataset_state_translation
// 翻译任务、wf_community_dataset_reward_pool 补奖励池，WorldState 代码簿翻译集团和节点。
public class SyndicateMissionsPlugin extends AbstractWorldStateCommandPlugin {
    public SyndicateMissionsPlugin() {
        super(state -> WarframeCommandData.syndicate(state, "集团任务", value -> true));
    }
}
