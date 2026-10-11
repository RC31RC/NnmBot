package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.PERSISTENT_ENEMIES, name = "小小黑",
        gameCode = "warframe", order = 67)
// 官方快照 PersistentEnemies；wf_community_worldstate_persistent_enemy 译敌人，
// wf_community_worldstate_node 译发现地点，不预测当前未发现的敌人位置。
public class PersistentEnemiesPlugin extends AbstractWorldStateCommandPlugin {
    public PersistentEnemiesPlugin() {
        super(WarframeCommandData::persistentEnemies);
    }
}
