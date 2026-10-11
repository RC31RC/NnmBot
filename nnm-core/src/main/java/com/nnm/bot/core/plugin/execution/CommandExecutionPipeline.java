package com.nnm.bot.core.plugin.execution;

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
    private BotGroupMemberIdentityResolver groupMemberIdentityResolver;

    /**
     * 按固定顺序执行访问控制、冷却、积分预占、插件处理和响应发送。
     *
     * <p>成功奖励在插件完成后结算，使最终响应可以包含变更后的余额；
     * 预扣积分在插件或发送阶段失败时退款。</p>
     */
    public void execute(CommandMatch match) {
        RuntimeCommand command = match.command();
        BotMessageContext context = match.invocation().context();
        CommandDescriptor descriptor = command.descriptor();
        CommandExecutionConfiguration configuration = command.configuration();
        if (!configuration.enabled()) {
            return;
        }

        this.groupMemberIdentityResolver.findOrCreateForCommand(context);

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
            var accessDecision = accessPolicy.check(context, descriptor, configuration);
            if (!accessDecision.allowed()) {
                log.debug("Command [{}] rejected by access policy for sender [{}]: {}",
                        descriptor.code(), context.senderId(), accessDecision.reason());
                return;
            }
        }

        CommandCooldownGuard cooldownGuard = this.cooldownGuardProvider.getIfAvailable();
        if (ObjectUtil.isNotNull(cooldownGuard)
                && !cooldownGuard.tryAcquire(context, descriptor, configuration.cooldownSeconds())) {
            log.debug("Command [{}] is cooling down for sender [{}]",
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
                log.debug("Command [{}] rejected by cost guard: {}",
                        descriptor.code(), reservation.message());
                this.responseDispatcher.dispatchNotice(context, reservation.message());
            }
            return;
        }

        try {
            CommandResult result = command.plugin().handle(match.invocation());
            if (ObjectUtil.isNotNull(costGuard)) {
                if (ObjectUtil.isNotNull(result) && result.successful()) {
                    result = costGuard.commit(context, descriptor, reservation, result);
                } else {
                    costGuard.refund(reservation, null);
                }
            }
            if (ObjectUtil.isNotNull(result)) {
                this.responseDispatcher.dispatch(context, command, result);
            }
        } catch (RuntimeException exception) {
            if (ObjectUtil.isNotNull(costGuard) && ObjectUtil.isNotNull(reservation) && reservation.reserved()) {
                costGuard.refund(reservation, exception);
            }
            throw exception;
        }
    }
}
