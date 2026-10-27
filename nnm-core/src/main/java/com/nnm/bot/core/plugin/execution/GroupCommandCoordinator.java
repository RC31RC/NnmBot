package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.service.BotGroupCommandContext;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityAdapters;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.group.service.GroupCommandService;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandExecutionContext;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot.Binding;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * 管理同 JVM 的文本关联、原生重放边界和绑定操作的后到身份补充。
 */
@Component
public class GroupCommandCoordinator {

    @Resource
    private BotGroupCommandContextResolver groups;
    @Resource
    private BotGroupMemberIdentityResolver members;
    @Resource
    private BotGroupMemberIdentityAdapters identityAdapters = BotGroupMemberIdentityAdapters.defaults();
    @Resource
    private CommandReceiptRegistry receipts;
    @Resource
    private ObjectProvider<GroupCommandService> groupCommands;

    public CommandMatch prepare(CommandMatch match) {
        BotMessageContext context = match.invocation().context();
        if (context.execution() != null) return match;
        CommandExecutionContext groupView = groups.executionView(context);
        CommandExecutionContext view = groupView.groupView() == null ? groupView
                : new CommandExecutionContext(groupView.groupView(), groupView.bindingView(), groupView.bindings(),
                        CommandExecutionContext.Member.of(members.findByContext(groupView.groupView().id(), context).orElse(null)));
        return new CommandMatch(match.command(), new CommandInvocation(context.withExecution(view),
                match.invocation().commandCode(), match.invocation().matchedAlias(), match.invocation().arguments()));
    }

    public boolean shouldIgnore(CommandMatch match) {
        BotMessageContext context = match.invocation().context();
        BotGroupCommandContext group = groups.resolve(context).orElse(null);
        if (group == null || !isCrossChannelMultiInstance(context, group.group().getId())) {
            return false;
        }
        String commandCode = normalizeCommand(match.invocation().commandCode());
        if (SystemCommandCodes.BIND.equals(commandCode)) {
            return !validBindingReceipt(context, match.invocation().arguments());
        }
        BotGroupMember member = members.findByContext(group.group().getId(), context).orElse(null);
        return member == null || !member.isEnabled()
                || !hasAllConfiguredIdentities(context, group.group().getId(), member);
    }

    public CommandMatch observe(CommandMatch match) {
        BotMessageContext context = match.invocation().context();
        BotGroupCommandContext group = groups.resolve(context).orElse(null);
        if (group == null || !isMultiInstance(context, group.group().getId())) {
            return match;
        }
        String fingerprint = correlationFingerprint(group, match);
        if (fingerprint == null) {
            return match;
        }
        String id = receipts.observeLocal(fingerprint, context);
        BotMessageContext correlated = context.withDispatchId(id);
        receipts.register(id, correlated);
        return new CommandMatch(match.command(), new CommandInvocation(correlated,
                match.invocation().commandCode(), match.invocation().matchedAlias(),
                match.invocation().arguments()));
    }

    public boolean claim(BotMessageContext context) {
        // 分组单 owner 保证这些线程同处一个 JVM；不再把轮次或原生消息 ID 写入 Redis。
        return receipts.claimLocal(context);
    }

    public boolean isReplay(BotMessageContext context) {
        return receipts.isReplay(context);
    }

    /**
     * OneBot 先到时，官方回执仍需补 OpenID，但不能再次执行插件或发送结果。
     */
    public void supplementBindingIfNeeded(CommandMatch match) {
        BotMessageContext context = match.invocation().context();
        if (!SystemCommandCodes.BIND.equals(normalizeCommand(match.invocation().commandCode()))
                || context.dispatchId() == null
                || !validBindingReceipt(context, match.invocation().arguments())) {
            return;
        }
        if (receipts.hasEarlierReceiptFromPlatform(context.dispatchId(), context)) {
            return;
        }
        GroupCommandService service = groupCommands.getIfAvailable();
        if (service != null) {
            service.bindQq(context, match.invocation().arguments().trim());
        }
    }

    private String correlationFingerprint(
            BotGroupCommandContext group, CommandMatch match) {
        BotMessageContext context = match.invocation().context();
        String commandCode = normalizeCommand(match.invocation().commandCode());
        String arguments = Objects.requireNonNullElse(match.invocation().arguments(), "").trim();
        String senderIdentity;
        if (SystemCommandCodes.BIND.equals(commandCode)) {
            if (!validBindingReceipt(context, arguments)) {
                return null;
            }
            senderIdentity = "bind-target:" + arguments;
        } else {
            BotGroupMember member = members.findByContext(group.group().getId(), context).orElse(null);
            if (member != null && member.getId() != null) {
                senderIdentity = "member:" + member.getId();
            } else {
                senderIdentity = "native:" + context.platform().name()
                        + ":" + context.senderId();
            }
        }
        return digest(String.join("|", Long.toString(group.group().getId()),
                senderIdentity, commandCode, arguments));
    }

    private List<BotGroupBot> enabledBindings(BotMessageContext context, long groupId) {
        return context.execution() == null ? groups.enabledBindings(groupId)
                : context.execution().bindings().stream().map(Binding::entity).toList();
    }

    private boolean isMultiInstance(BotMessageContext context, long groupId) {
        return enabledBindings(context, groupId).size() > 1;
    }

    private boolean isCrossChannelMultiInstance(BotMessageContext context, long groupId) {
        List<BotGroupBot> bindings = enabledBindings(context, groupId);
        return bindings.size() > 1
                && bindings.stream().map(binding -> binding.getPlatform().toUpperCase(Locale.ROOT))
                        .distinct().count() > 1;
    }

    private boolean hasAllConfiguredIdentities(BotMessageContext context, long groupId, BotGroupMember member) {
        return enabledBindings(context, groupId).stream()
                .map(binding -> BotPlatform.valueOf(binding.getPlatform().toUpperCase(Locale.ROOT)))
                .distinct()
                .allMatch(platform -> identityAdapters.hasIdentity(platform, member));
    }

    private boolean validBindingReceipt(BotMessageContext context, String arguments) {
        String qq = arguments == null ? "" : arguments.trim();
        if (!qq.matches("\\d{5,20}")) {
            return false;
        }
        return context.platform() != BotPlatform.ONEBOT || qq.equals(context.senderId());
    }

    private static String normalizeCommand(String command) {
        return command == null ? "" : command.strip().toLowerCase(Locale.ROOT);
    }

    static String nativeIdentity(BotMessageContext context) {
        if (context.messageId() == null || context.messageId().isBlank()) return null;
        return digest(Stream.of(context.platform().name(), context.scope().name(),
                        context.botAccountId(), context.conversationId(), context.messageId())
                .map(value -> value.length() + ":" + value).collect(Collectors.joining()));
    }

    public static String digest(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
