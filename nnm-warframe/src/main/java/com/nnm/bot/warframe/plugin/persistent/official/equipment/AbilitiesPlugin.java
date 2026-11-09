package com.nnm.bot.warframe.plugin.persistent.official.equipment;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.WarframeEquipmentCommandDTO;

@BotPluginCommand(code = WarframeCommandCodes.ABILITIES, matchType = MatchType.PREFIX)
public final class AbilitiesPlugin extends AbstractEquipmentPlugin {
    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, WarframeEquipmentCommandDTO parsed) {
        return equipment(Category.ABILITY, "技能资料", parsed);
    }
}
