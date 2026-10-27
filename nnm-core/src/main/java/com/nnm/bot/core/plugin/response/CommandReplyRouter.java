package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.group.service.BotGroupCommandContext;
import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.channel.model.BotChannelTarget;
import com.nnm.bot.core.channel.port.BotReplyContextProvider;
import com.nnm.bot.core.channel.runtime.BotChannelTargetSelector;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityAdapters;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.sender.BotMessageSenderRouter;
import com.nnm.bot.core.plugin.execution.CommandReceiptRegistry;
import com.nnm.bot.core.plugin.api.CommandAccessPolicy;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/** Selection finishes before formatting or sending. Delivery is never retried on another instance. */
@Component
public class CommandReplyRouter {
    @Resource
    private BotGroupCommandContextResolver groups;
    @Resource
    private BotGroupMemberIdentityResolver members;
    @Resource
    private BotChannelTargetSelector targetSelector;
    @Resource
    private List<BotReplyContextProvider> contextProviders;
    @Resource
    private CommandReceiptRegistry receipts;
    @Resource
    private BotGroupMemberIdentityAdapters identityAdapters = BotGroupMemberIdentityAdapters.defaults();
    @Resource
    private ObjectProvider<CommandAccessPolicy> accessPolicyProvider;
    @Resource
    private ReplyPlanner planner;
    @Resource
    private ObjectProvider<ClusterBotRuntime> clusterProvider;
    @Resource
    private BotMessageSenderRouter senders;

    public void validate(Selection selection, CommandDescriptor descriptor) {
        BotMessageContext context = selection.context();
        if (!clusterProvider.getObject().isLocalOwner(new BotChannelKey(context.platform(), context.botAccountId()))) {
            throw new BusinessException(BotChannelErrorCode.REPLY_INSTANCE_UNAVAILABLE);
        }
        Optional<BotGroupCommandContext> group = groups.resolve(context);
        if (group.isPresent() && targetSelector.candidates(group.get().group().getId()).stream().noneMatch(target ->
                target.instance().platform() == context.platform()
                        && target.instance().botAccountId().equals(context.botAccountId())
                        && target.binding().getScopeKey().equals(context.conversationId()))) {
            throw new BusinessException(BotChannelErrorCode.REPLY_INSTANCE_UNAVAILABLE);
        }
        CommandAccessPolicy policy = accessPolicyProvider.getIfAvailable();
        if (descriptor != null && policy != null
                && !policy.checkInstance(context.platform(), context.botAccountId(), descriptor).allowed()) {
            throw new BusinessException(BotChannelErrorCode.REPLY_INSTANCE_UNAVAILABLE);
        }
    }

    public BotMessageSendPermit reserveFallback(BotMessageContext context) {
        return senders.reserve(context, 1).getFirst();
    }

    public Selection select(BotMessageContext origin,
                                    CommandDescriptor descriptor,
                                    CommandExecutionConfiguration configuration,
                                    CommandResult result) {
        BotMessageContext prepared = origin;
        Optional<BotGroupCommandContext> group = groups.resolve(prepared);
        if (group.isEmpty()) return evaluate(origin, configuration, result)
                .orElseThrow(() -> new BusinessException(BotChannelErrorCode.REPLY_INSTANCE_UNAVAILABLE));
        Long groupId = group.get().group().getId();
        BotGroupMember member = members.findByContext(groupId, prepared).orElse(null);
        List<BotMessageContext> observed = new ArrayList<>();
        observed.add(origin);
        if (origin.dispatchId() != null) observed.addAll(receipts.receipts(origin.dispatchId()));
        List<BotChannelTarget> candidates = prepared.execution() == null ? targetSelector.candidates(groupId)
                : targetSelector.candidates(groupId, prepared.execution().bindings());
        for (BotChannelTarget candidate : candidates) {
            BotGroupBot binding = candidate.binding();
            BotChannelInstanceMetadata record = candidate.instance();
            if (descriptor != null && accessPolicyProvider != null) {
                CommandAccessPolicy accessPolicy = accessPolicyProvider.getIfAvailable();
                if (accessPolicy != null
                        && !accessPolicy.checkInstance(
                                record.platform(), record.botAccountId(), descriptor).allowed()) {
                    continue;
                }
            }
            Optional<BotMessageContext> target = observed.stream()
                    .filter(value -> value.platform() == record.platform()
                            && value.botAccountId().equals(record.botAccountId())
                            && value.conversationId().equals(binding.getScopeKey())).findFirst();
            if (target.isEmpty()) {
                String sender = senderId(member, record.platform());
                target = contextProviders.stream().filter(provider -> provider.platform() == record.platform())
                        .findFirst().flatMap(provider -> provider.create(record, binding, origin, sender));
            }
            if (target.isPresent()) {
                BotMessageContext selected = origin.execution() == null ? target.get() : target.get().withExecution(origin.execution());
                Optional<Selection> selection = evaluate(selected, configuration, result);
                if (selection.isPresent()) return selection.get();
            }
        }
        throw new BusinessException(BotChannelErrorCode.REPLY_INSTANCE_UNAVAILABLE);
    }

    private Optional<Selection> evaluate(BotMessageContext context,
                                         CommandExecutionConfiguration configuration,
                                         CommandResult result) {
        Optional<List<ResponsePlan>> planned = planner.plan(context, configuration, result);
        if (planned.isEmpty()) return Optional.empty();
        List<BotMessageSendPermit> permits;
        try {
            permits = senders.reserve(context, planner.platformMessageCount(context, planned.get()));
        } catch (BotMessageNotSentException unavailable) {
            return Optional.empty();
        }
        return Optional.of(new Selection(context, planned.get(), permits));
    }

    public record Selection(BotMessageContext context, List<ResponsePlan> plans,
                            List<BotMessageSendPermit> permits) {
        public Selection {
            plans = List.copyOf(plans);
            permits = List.copyOf(permits);
        }
    }

    private String senderId(BotGroupMember member, BotPlatform platform) {
        if (member == null) return null;
        return identityAdapters.identity(platform, member);
    }
}
