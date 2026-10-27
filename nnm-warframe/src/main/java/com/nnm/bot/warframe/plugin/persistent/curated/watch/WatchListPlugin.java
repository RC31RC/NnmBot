package com.nnm.bot.warframe.plugin.persistent.curated.watch;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.service.BotGroupCommandContext;
import java.util.Optional;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeGroupWatchRuleDTO;
import com.nnm.bot.warframe.persistent.curated.service.watch.WarframeGroupWatchRuleService;
import jakarta.annotation.Resource;
import java.util.List;

/** 查看当前群成员自己的 Warframe 蹲任务规则。 */
@BotPluginCommand(
        code = WarframeCommandCodes.WATCH_LIST,
        matchType = MatchType.EXACT,
        name = "蹲任务",
        gameCode = "warframe",
        order = 77,
        allowRelatedAction = true)
// 只读取当前群组成员自己的 wf_group_watch_rule，不暴露同群其他成员的规则。
public final class WatchListPlugin extends BotPlugin {

    @Resource
    private BotGroupCommandContextResolver groupResolver;
    @Resource
    private BotGroupMemberIdentityResolver memberResolver;
    @Resource
    private WarframeGroupWatchRuleService ruleService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        Optional<BotGroupCommandContext> group = groupResolver.resolve(invocation.context());
        if (group.isEmpty()) {
            return CommandResult.rejected("蹲任务只能在已绑定逻辑群组的群聊中使用");
        }
        Optional<BotGroupMember> member = memberResolver.findByContext(
                group.get().group().getId(), invocation.context());
        if (member.isEmpty()) {
            return CommandResult.rejected("当前用户还未加入该逻辑群组");
        }
        List<WarframeGroupWatchRuleDTO> rules = ruleService.findEnabledRules(
                group.get().group().getId(), member.get().getId());
        if (CollUtil.isEmpty(rules)) {
            return CommandResult.text("当前没有启用的蹲任务");
        }
        StringBuilder text = new StringBuilder("当前蹲任务：");
        rules.forEach(rule -> text.append("\n- ")
                .append(rule.getWatchType())
                .append(" / ")
                .append(rule.getWatchKey())
                .append(" / ")
                .append(rule.getWatchValue()));
        return CommandResult.text(text.toString());
    }
}
