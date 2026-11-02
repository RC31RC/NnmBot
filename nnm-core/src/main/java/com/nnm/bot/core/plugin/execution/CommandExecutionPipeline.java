package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.common.resource.ResourceScope;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.api.CommandAccessPolicy;
import com.nnm.bot.core.plugin.api.CommandCooldownGuard;
import com.nnm.bot.core.plugin.api.CommandCostGuard;
import com.nnm.bot.core.plugin.model.CommandAccessDecision;
import com.nnm.bot.core.plugin.model.CommandCostReservation;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import io.github.rdt.lock.RdtLock;
import jakarta.annotation.Resource;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** 命令执行边界：事务外准入和准备，事务内原子结算积分及业务写入。 */
@Slf4j
@Component
public class CommandExecutionPipeline {

    /** 权限实现由 admin 提供；core 独立运行时仅放行系统命令。 */
    @Resource
    private ObjectProvider<CommandAccessPolicy> accessPolicyProvider;
    /** 准备完成后按稳定命令码复核最新配置，禁用或移除立即生效。 */
    @Resource
    private CommandRegistry registry;
    /** 冷却只在准入时消耗一次，事务内复核不再次申请。 */
    @Resource
    private CommandCooldownGuard cooldownGuard;
    /** 积分预扣、业务结果结算及独立投递补偿。 */
    @Resource
    private CommandCostGuard costGuard;
    /** 同群同机的文字轮次关联与原子认领。 */
    @Resource
    private GroupCommandCoordinator coordinator;

    /**
     * 在准备前检查权限并认领，防止同轮回执重复下载、查询引用或 OCR。
     *
     * @param match 已按现有文字规则关联的命令
     * @return 是否取得本次执行权；失败者不进入准备或业务事务
     */
    public boolean admit(CommandMatch match) {
        if (!allowed(match))
            return false;
        BotMessageContext context = match.invocation().context();
        if (coordinator != null && !coordinator.claim(context)) {
            coordinator.supplementBindingIfNeeded(match);
            return false;
        }
        return cooldownGuard.tryAcquire(context, match.command().descriptor(),
            match.command().configuration().cooldownSeconds());
    }

    /**
     * 经 Spring 代理执行业务写事务，扣费、Plugin 写入与奖励共同提交或回滚。
     *
     * @param prepared 已完成事务外准备并绑定具体输入类型的命令
     * @param resources 调用入口拥有的资源，生命周期覆盖事务代理最终提交及回复发送
     * @return 业务结果与积分引用；准备期间配置失效时返回 null
     */
    @RdtLock
    @Transactional(rollbackFor = Exception.class)
    public CommandExecutionOutcome execute(PreparedCommand<?> prepared, ResourceScope resources) {
        CommandMatch initial = prepared.match();
        RuntimeCommand latest = registry.command(initial.invocation().commandCode()).orElse(null);
        if (latest == null || latest.plugin() != prepared.plugin())
            return null;
        CommandMatch match = new CommandMatch(latest, initial.invocation());
        // 准备可能耗时；只复核条件，不再次认领或消耗冷却。
        if (!allowed(match))
            return null;
        BotMessageContext context = match.invocation().context();
        CommandDescriptor descriptor = match.command().descriptor();
        CommandCostReservation reservation = costGuard.reserve(context, descriptor);
        if (reservation == null || !reservation.allowed()) {
            String message = reservation == null ? null : reservation.message();
            throw new CommandRejectedException(
                message == null || message.isBlank() ? null : CommandResult.rejected(message));
        }
        CommandResult result = prepared.execute();
        if (result != null)
            resources.addAll(result.resources());
        if (result == null || !result.successful())
            throw new CommandRejectedException(result);
        result = costGuard.commit(context, descriptor, reservation, result);
        if (result != null)
            resources.addAll(result.resources());
        if (result == null || !result.successful()) {
            throw new IllegalStateException("积分结算必须返回成功的命令结果");
        }
        return new CommandExecutionOutcome(result, reservation);
    }

    /**
     * 业务已提交、必要回复确定未发送时，按原扣费引用独立幂等退回消耗。
     *
     * @param reservation 已提交的积分预扣引用
     * @param failure 投递异常；无异常的未发送报告可为 null
     */
    @RdtLock
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void refundUnsentCommandCost(CommandCostReservation reservation, Throwable failure) {
        if (reservation.reserved())
            costGuard.refund(reservation, failure);
    }

    /** 检查当前实例和用户是否仍允许执行；不申请锁、扣费、认领或消耗冷却。 */
    private boolean allowed(CommandMatch match) {
        RuntimeCommand command = match.command();
        CommandExecutionConfiguration configuration = command.configuration();
        if (!configuration.enabled() || (coordinator != null && coordinator.shouldIgnore(match)))
            return false;
        CommandAccessPolicy policy = accessPolicyProvider.getIfAvailable();
        if (policy == null) {
            return SystemCommandCodes.ALL.contains(command.descriptor().code().strip().toLowerCase(Locale.ROOT));
        }
        CommandAccessDecision decision =
            policy.check(match.invocation().context(), command.descriptor(), configuration);
        return decision.allowed();
    }
}
