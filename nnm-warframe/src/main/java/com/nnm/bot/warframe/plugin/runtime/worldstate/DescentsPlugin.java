package com.nnm.bot.warframe.plugin.runtime.worldstate;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeDescendiaService;

/** 查询当前沉沦之地及本周人工确认的楼层奖励。 */
@BotPluginCommand(code = WarframeCommandCodes.DESCENTS)
public class DescentsPlugin extends AbstractWorldStateCommandPlugin {
    /** @param service 管理端与命令共用的沉沦之地投影服务。 */
    public DescentsPlugin(WarframeDescendiaService service) {
        super(service::project);
    }
}
