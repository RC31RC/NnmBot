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
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeGroupWatchRuleDTO;
import com.nnm.bot.warframe.persistent.curated.service.watch.WarframeGroupWatchRuleService;
import jakarta.annotation.Resource;

/** 创建 Warframe 事件订阅。 */
@BotPluginCommand(code = WarframeCommandCodes.SUBSCRIBE, matchType = MatchType.PREFIX, name = "订阅", gameCode = "warframe", order = 75)
// bot_group_bot、bot_group 校验当前群和机器人绑定；bot_group_member 解析或创建调用者；
// wf_group_watch_rule 保存订阅规则。渠道实例身份由 Provider 在内存边界解析。
public final class SubscribePlugin extends BotPlugin {

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
        WarframeSubscriptionSpec parsed = WarframeSubscriptionSpec.parse(invocation.arguments());
        if (parsed == null) {
            return CommandResult.rejected("订阅格式：订阅 主题 [条件 ...]，例如：订阅平原、订阅仲裁 谷神防御 谷神拦截、订阅裂缝 钢铁虚空捕获");
        }
        Optional<BotGroupCommandContext> group = groupResolver.resolve(invocation.context());
        if (group.isEmpty()) {
            return CommandResult.rejected("订阅只能在已绑定逻辑群组的群聊中使用");
        }
        Optional<BotGroupMember> member = memberResolver.findOrCreateForWrite(
                group.get().group().getId(), invocation.context());
        if (member.isEmpty()) {
            return CommandResult.rejected("当前用户还未加入该逻辑群组");
        }
        WarframeGroupWatchRuleDTO rule = ruleService.create(
                group.get().group().getId(), member.get().getId(),
                parsed.type(), parsed.key(), parsed.value());
        return CommandResult.text("订阅成功：" + rule.getWatchType() + " / "
                + rule.getWatchKey() + " / " + rule.getWatchValue());
    }

}
