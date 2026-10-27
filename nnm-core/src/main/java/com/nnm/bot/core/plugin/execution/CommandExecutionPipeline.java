package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.plugin.model.CommandAccessDecision;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.plugin.model.DeliveryReport;
import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.plugin.api.CommandAccessPolicy;
import com.nnm.bot.core.plugin.api.CommandCostGuard;
import com.nnm.bot.core.plugin.api.CommandCooldownGuard;
import com.nnm.bot.core.plugin.model.CommandCostReservation;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.response.CommandResponseDispatcher;
import com.nnm.bot.core.plugin.response.ReplyDeliveryException;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 命令统一执行链：配置、能力权限、冷却、可选积分、插件和响应。
 */
@Slf4j
@Component
public class CommandExecutionPipeline {

    @Resource
    private CommandResponseDispatcher responseDispatcher;
    @Resource
    private ObjectProvider<CommandAccessPolicy> accessPolicyProvider;
    @Resource
    private ObjectProvider<CommandCooldownGuard> cooldownGuardProvider;
    @Resource
    private ObjectProvider<CommandCostGuard> costGuardProvider;
    @Resource
    private GroupCommandCoordinator coordinator;
    /** 保留显式身份依赖，成员建立只在写入边界完成。 */
    @Resource
    private BotGroupMemberIdentityResolver groupMemberIdentityResolver;

    /**
     * 按固定顺序执行访问控制、冷却、积分预占、插件处理和响应发送。
     *
     * <p>成功奖励在插件完成后结算，使最终响应可以包含变更后的余额；
     * 业务失败或必要内容确定未发送时退回预扣；部分接受、未知、可选增强及清理失败不退款。
     * 成功奖励属于业务结算，不因平台回执未知而撤销。</p>
     */
    public void execute(CommandMatch match) {
        RuntimeCommand command = match.command();
        BotMessageContext context = match.invocation().context();
        CommandDescriptor descriptor = command.descriptor();
        CommandExecutionConfiguration configuration = command.configuration();
        if (!configuration.enabled()) {
            log.info("Command skipped: disabled command={} messageId={}", descriptor.code(), context.messageId());
            return;
        }

        if (coordinator != null && coordinator.shouldIgnore(match)) {
            log.info("Command skipped: member identity policy command={} messageId={}", descriptor.code(), context.messageId());
            return;
        }

        CommandAccessPolicy accessPolicy = this.accessPolicyProvider.getIfAvailable();
        if (ObjectUtil.isNull(accessPolicy)) {
            String commandCode = descriptor.code().strip().toLowerCase(Locale.ROOT);
            if (!SystemCommandCodes.ALL.contains(commandCode)) {
                // 管理模块或权限策略装配异常时必须默认拒绝，不能让业务命令绕过能力角色校验。
                log.warn("Command [{}] rejected because no access policy is available for sender [{}]",
                        descriptor.code(), context.senderId());
                return;
            }
        } else {
            CommandAccessDecision accessDecision = accessPolicy.check(context, descriptor, configuration);
            if (!accessDecision.allowed()) {
                log.info("Command [{}] rejected by access policy for sender [{}]: {}",
                        descriptor.code(), context.senderId(), accessDecision.reason());
                return;
            }
        }

        // An unauthorized receiver must not consume the shared execution claim.
        if (coordinator != null && !coordinator.claim(context)) {
            log.info("Command skipped: execution already claimed command={} messageId={}", descriptor.code(), context.messageId());
            coordinator.supplementBindingIfNeeded(match);
            return;
        }

        CommandCooldownGuard cooldownGuard = this.cooldownGuardProvider.getIfAvailable();
        if (ObjectUtil.isNotNull(cooldownGuard)
                && !cooldownGuard.tryAcquire(context, descriptor, configuration.cooldownSeconds())) {
            log.info("Command [{}] is cooling down for sender [{}]",
                    descriptor.code(), context.senderId());
            return;
        }

        CommandCostGuard costGuard = this.costGuardProvider.getIfAvailable();
        CommandCostReservation reservation = ObjectUtil.isNull(costGuard)
                ? CommandCostReservation.none()
                : costGuard.reserve(context, descriptor);
        if (ObjectUtil.isNull(reservation) || !reservation.allowed()) {
            if (ObjectUtil.isNotNull(reservation) && ObjectUtil.isNotNull(reservation.message())
                    && !reservation.message().isBlank()) {
                log.info("Command [{}] rejected by cost guard: {}",
                        descriptor.code(), reservation.message());
                this.responseDispatcher.dispatchNotice(context, reservation.message());
            }
            return;
        }

        CommandResult result;
        try {
            result = command.plugin().handle(match.invocation());
        } catch (RuntimeException | Error failure) {
            refund(costGuard, reservation, failure);
            throw failure;
        }
        if (result == null) {
            refund(costGuard, reservation, null);
            return;
        }
        if (!result.successful()) {
            try {
                refund(costGuard, reservation, null);
            } catch (RuntimeException | Error failure) {
                // A rejected result has not reached the dispatcher; close its attachments if refund fails first.
                closeResultResources(result, failure);
                throw failure;
            }
        } else if (costGuard != null) {
            try {
                result = costGuard.commit(context, descriptor, reservation, result);
            } catch (RuntimeException | Error failure) {
                // 结果尚未交给分发器，结算异常时资源仍归执行链所有。
                closeResultResources(result, failure);
                refund(costGuard, reservation, failure);
                throw failure;
            }
        }
        try {
            DeliveryReport delivery = responseDispatcher.dispatch(context, command, result);
            if (result.successful() && delivery.definitelyUnsent()) refund(costGuard, reservation, null);
            log.info("Command response command={} messageId={} businessSuccess={} delivery={}",
                    descriptor.code(), context.messageId(), result.successful(), delivery.outcome());
        } catch (ReplyDeliveryException failure) {
            if (result.successful() && failure.report().definitelyUnsent()) refund(costGuard, reservation, failure);
            throw failure;
        }
    }

    private void refund(CommandCostGuard guard, CommandCostReservation reservation, Throwable failure) {
        if (guard == null || !reservation.reserved()) return;
        if (failure == null) {
            guard.refund(reservation, null);
            return;
        }
        try {
            guard.refund(reservation, failure);
        } catch (RuntimeException refundFailure) {
            failure.addSuppressed(refundFailure);
        }
    }

    private void closeResultResources(CommandResult result, Throwable failure) {
        for (CommandAttachment resource : result.resources()) {
            try {
                resource.close();
            } catch (RuntimeException cleanup) {
                failure.addSuppressed(cleanup);
            }
        }
    }
}
