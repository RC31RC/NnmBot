package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

@BotPluginCommand(code = WarframeCommandCodes.PERSISTENT_ENEMIES, name = "小小黑",
        gameCode = "warframe", order = 67,
        allowRelatedAction = true)
public class PersistentEnemiesPlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造常驻敌人插件，并保存初始化参数。
     */
    public PersistentEnemiesPlugin() {
        super(WarframeWorldStateProjector::persistentEnemies);
    }
}
