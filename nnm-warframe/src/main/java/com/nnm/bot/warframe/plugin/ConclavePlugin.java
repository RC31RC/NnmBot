package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** Conclave 当前挑战命令。 */
@BotPluginCommand(code = WarframeCommandCodes.CONCLAVE, name = "竞技场",
        gameCode = "warframe", order = 61)
// 官方快照 PVPChallengeInstances；conclaveData 规则由 wf_community_worldstate_rule
// 提供模式、分类、标题、说明和声望映射，不新增动态业务表。
public class ConclavePlugin extends AbstractWorldStateCommandPlugin {
    public ConclavePlugin() {
        super(WarframeCommandData::conclave);
    }
}
