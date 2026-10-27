package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;

/** Conclave 当前挑战命令。 */
@BotPluginCommand(code = WarframeCommandCodes.CONCLAVE, name = "竞技场",
        gameCode = "warframe", order = 61,
        allowRelatedAction = true)
// 提供模式、分类、标题、说明和声望映射，不新增动态业务表。
public class ConclavePlugin extends AbstractWorldStateCommandPlugin {
    /**
     * 构造武形秘仪插件，并保存初始化参数。
     */
    public ConclavePlugin() {
        super(WarframeWorldStateProjector::conclave);
    }
}
