package com.nnm.bot.admin.adapter.command;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandGroupMember;
import com.nnm.bot.core.group.service.BotGroupCommandContext;
import java.util.Optional;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandGroup;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupPreviewDTO;
import com.nnm.bot.admin.repository.bot.command.BotCommandGroupMemberRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandGroupRepository;
import com.nnm.bot.admin.command.RelatedCommandBuilder;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.api.CommandAccessPolicy;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.core.plugin.response.CommandRelatedActionProvider;
import com.nnm.bot.core.qqofficial.config.QqReplyProperties;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** 运行时导航与管理预览共享生成和身份过滤，不调用目标命令。 */
@Component
public class DatabaseCommandRelatedActionProvider implements CommandRelatedActionProvider {
    @Resource
    private BotCommandGroupRepository groups;
    @Resource
    private BotCommandGroupMemberRepository members;
    @Resource
    private CommandRegistry commands;
    @Resource
    private CommandAccessPolicy accessPolicy;
    @Resource
    private RelatedCommandBuilder builder;
    @Resource
    private ReplyProperties limits;
    @Resource
    private QqReplyProperties qq;
    @Resource
    private BotGroupCommandContextResolver groupContexts;
    @Resource
    private BotGroupMemberIdentityResolver groupMembers;

    @Override
    public List<ReplyAction> actions(BotMessageContext context, RuntimeCommand current, CommandResult result) {
        if (context == null || !result.successful()) return List.of();
        BotCommandGroupMember membership = members.findAllByCommandCodeIgnoreCaseOrderBySortOrderAscIdAsc(current.descriptor().code())
                .stream().findFirst().orElse(null);
        if (membership == null) return List.of();
        BotCommandGroup group = groups.findById(membership.getGroupId()).orElse(null);
        return group == null ? List.of() : preview(context, group, current, result.actionContext()).stream()
                .filter(item -> item.reason().isEmpty()).map(item -> (ReplyAction) new ReplyAction.Command(item.title(), item.command(), false)).toList();
    }

    public List<BotCommandGroupPreviewDTO> preview(BotMessageContext context, BotCommandGroup group,
                                                   RuntimeCommand current, Map<String, String> actionContext) {
        Map<String, String> safe = actionContext.entrySet().stream()
                .filter(entry -> current.plugin().actionContextKeys().contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<BotCommandGroupPreviewDTO> rows = new ArrayList<>();
        String scopeReason = "";
        if (context.isGroupMessage()) {
            Optional<BotGroupCommandContext> scope = groupContexts.resolve(context);
            if (scope.isEmpty()) scopeReason = "当前群组或机器人绑定未启用";
            else if (groupMembers.findByContext(scope.get().group().getId(), context)
                    .filter(member -> !member.isEnabled()).isPresent()) scopeReason = "当前群组成员已停用";
        }
        int displayed = 0;
        for (BotCommandGroupMember member : members.findAllByGroupIdOrderBySortOrderAscIdAsc(group.getId())) {
            String reason = "";
            String text = "";
            RuntimeCommand target = commands.command(member.getCommandCode()).orElse(null);
            if (!group.isEnabled()) reason = "关联组已停用";
            else if (!current.configuration().enabled()) reason = "来源命令已停用";
            else if (!scopeReason.isEmpty()) reason = scopeReason;
            else if (member.getCommandCode().equalsIgnoreCase(current.descriptor().code())) reason = "当前来源命令";
            else if (target == null) reason = "插件未注册";
            else if (!target.configuration().enabled()) reason = "目标命令已停用";
            else if (!commands.metadata(target.plugin()).allowRelatedAction()) reason = "目标不允许关联导航";
            else if (!accessPolicy.allowed(context, target.descriptor(), target.configuration())) reason = "当前用户、群组或机器人无处理资格";
            else {
                try {
                    text = builder.command(target, member.getParameterBindings(), safe);
                    if (displayed >= limits.getMaxActions()) reason = "超过导航数量限制";
                    else displayed++;
                } catch (IllegalArgumentException invalid) {
                    reason = invalid.getMessage();
                }
            }
            boolean nativeCandidate = context.platform() == BotPlatform.QQ_OFFICIAL
                    && qq.keyboardAllowed(context.botAccountId(), context.scope())
                    && current.configuration().presentation().resolve(context.platform(), context.scope()).interaction() == InteractionPolicy.PREFER_NATIVE;
            rows.add(new BotCommandGroupPreviewDTO(current.descriptor().code(), member.getCommandCode(), member.getTitle(),
                    text, reason, nativeCandidate ? "原生键盘候选，须本次正文支持同条组合" : "命令文本", text));
        }
        return List.copyOf(rows);
    }
}
