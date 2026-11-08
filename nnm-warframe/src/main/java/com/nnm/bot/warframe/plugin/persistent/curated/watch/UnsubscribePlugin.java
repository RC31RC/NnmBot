package com.nnm.bot.warframe.plugin.persistent.curated.watch;

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
import com.nnm.bot.warframe.persistent.curated.service.watch.WarframeGroupWatchRuleService;
import jakarta.annotation.Resource;

/** 取消 Warframe 事件订阅。 */
@BotPluginCommand(code = WarframeCommandCodes.UNSUBSCRIBE, matchType = MatchType.PREFIX)
// bot_group_bot、bot_group 校验当前群和机器人绑定；bot_group_member 定位调用者；
// wf_group_watch_rule 查找并停用匹配订阅规则。渠道实例身份由 Provider 在内存边界解析。
public final class UnsubscribePlugin extends BotPlugin<Void, WarframeSubscriptionSpec> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeSubscriptionSpec parse(CommandInvocation invocation, Void prepared) {
        return WarframeSubscriptionSpec.parse(invocation.arguments());
    }

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
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, WarframeSubscriptionSpec parsed) {
        if (parsed == null) {
            return CommandResult.rejected("取消订阅格式：取消订阅 主题 [条件 ...]，条件与创建订阅时相同");
        }
        Optional<BotGroupCommandContext> group = groupResolver.resolve(invocation.context());
        if (group.isEmpty()) {
            return CommandResult.rejected("取消订阅只能在已绑定逻辑群组的群聊中使用");
        }
        Optional<BotGroupMember> member =
            memberResolver.findByContext(group.get().group().getId(), invocation.context());
        if (member.isEmpty()) {
            return CommandResult.rejected("当前用户还未加入该逻辑群组");
        }
        boolean disabled = ruleService.disable(group.get().group().getId(), member.get().getId(), parsed.type(),
            parsed.key(), parsed.value());
        return CommandResult.text(disabled ? "订阅已取消" : "没有找到匹配的启用订阅");
    }

}
