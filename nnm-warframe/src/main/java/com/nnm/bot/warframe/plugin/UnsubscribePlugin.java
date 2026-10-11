package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.application.WarframeGroupWatchRuleService;
import jakarta.annotation.Resource;

/** 取消 Warframe 事件订阅。 */
@BotPluginCommand(
        code = WarframeCommandCodes.UNSUBSCRIBE,
        matchType = MatchType.PREFIX,
        name = "取消订阅",
        gameCode = "warframe",
        order = 76)
// bot_group_bot、bot_group 校验当前群和机器人绑定；bot_group_member 定位调用者；
// wf_group_watch_rule 查找并停用匹配订阅规则。渠道实例身份由 Provider 在内存边界解析。
public final class UnsubscribePlugin extends BotPlugin {

    @Resource
    private BotGroupCommandContextResolver groupResolver;
    @Resource
    private BotGroupMemberIdentityResolver memberResolver;
    @Resource
    private WarframeGroupWatchRuleService ruleService;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        ParsedRule parsed = ParsedRule.parse(invocation.arguments());
        if (parsed == null) {
            return CommandResult.rejected("取消订阅格式：取消订阅 类型 [字段] 值");
        }
        var group = groupResolver.resolve(invocation.context());
        if (group.isEmpty()) {
            return CommandResult.rejected("取消订阅只能在已绑定逻辑群组的群聊中使用");
        }
        var member = memberResolver.findByContext(
                group.get().group().getId(), invocation.context());
        if (member.isEmpty()) {
            return CommandResult.rejected("当前用户还未加入该逻辑群组");
        }
        boolean disabled = ruleService.disable(
                group.get().group().getId(), member.get().getId(),
                parsed.type(), parsed.key(), parsed.value());
        return CommandResult.text(disabled ? "订阅已取消" : "没有找到匹配的启用订阅");
    }

    private record ParsedRule(String type, String key, String value) {
        static ParsedRule parse(String arguments) {
            String input = arguments == null ? "" : arguments.trim();
            if (input.isBlank()) {
                return null;
            }
            String[] parts = input.split("\\s+", 3);
            if (parts.length == 2) {
                return new ParsedRule(parts[0], "*", parts[1]);
            }
            if (parts.length == 3 && !parts[0].isBlank()
                    && !parts[1].isBlank() && !parts[2].isBlank()) {
                return new ParsedRule(parts[0], parts[1], parts[2]);
            }
            return null;
        }
    }
}
