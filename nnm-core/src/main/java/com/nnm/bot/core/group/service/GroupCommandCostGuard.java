package com.nnm.bot.core.group.service;

import cn.hutool.core.util.ObjectUtil;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandCostConfiguration;
import com.nnm.bot.core.plugin.api.CommandCostConfigurationProvider;
import com.nnm.bot.core.plugin.api.CommandCostGuard;
import com.nnm.bot.core.plugin.model.CommandCostReservation;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.group.domain.BotGroupMember;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 群组命令积分守卫。余额、扣费、成功奖励和退款均以逻辑群组成员为边界。
 */
@Component
public class GroupCommandCostGuard implements CommandCostGuard {

    @Resource
    private CommandCostConfigurationProvider costConfigurationProvider;
    @Resource
    private BotGroupCommandContextResolver groupContextResolver;
    @Resource
    private BotGroupMemberIdentityResolver memberIdentityResolver;
    @Resource
    private PointAccountService pointAccountService;
    @Resource
    private CommandPointPolicy commandPointPolicy;

    @Override
    @Transactional
    public CommandCostReservation reserve(BotMessageContext context,
                                          CommandDescriptor descriptor) {
        CommandCostConfiguration cost =
                costConfigurationProvider.configuration(descriptor.code());
        if (ObjectUtil.isNull(cost) || !cost.active()) {
            return CommandCostReservation.none();
        }
        if (!context.isGroupMessage()) {
            return denied("该命令只能在已配置的群组中使用");
        }

        BotGroupCommandContext groupContext = groupContextResolver.resolve(context).orElse(null);
        if (ObjectUtil.isNull(groupContext)) {
            return denied("当前群聊尚未绑定逻辑群组");
        }

        BotGroupMember member = memberIdentityResolver
                .findOrCreateForCommand(groupContext.group().getId(), context)
                .filter(BotGroupMember::isEnabled)
                .orElse(null);
        if (ObjectUtil.isNull(member)) {
            return denied("当前用户尚未加入该逻辑群组");
        }

        String costKey = cost.chargeable()
                ? commandPointPolicy.costIdempotencyKey(
                        context, descriptor, groupContext.group().getId(), member.getId())
                : null;
        String rewardKey = cost.rewardable()
                ? commandPointPolicy.rewardIdempotencyKey(
                        context, descriptor, groupContext.group().getId(), member.getId())
                : null;
        boolean repeatedReward = exists(rewardKey);
        if (exists(costKey)
                || (repeatedReward
                && !commandPointPolicy.allowsRepeatedReward(descriptor))) {
            return denied("该消息已经处理过");
        }

        boolean skipRepeatedRewardCost = repeatedReward
                && commandPointPolicy.allowsRepeatedReward(descriptor);
        boolean reserved = false;
        if (cost.chargeable() && !skipRepeatedRewardCost) {
            PointChangeResult spendResult = pointAccountService.spend(new PointOperation(
                    groupContext.group().getId(),
                    member.getId(),
                    descriptor.code(),
                    "COMMAND_COST",
                    cost.costPoints(),
                    costKey,
                    "命令积分消耗"));
            if (!spendResult.allowed()) {
                return denied(spendResult.message());
            }
            if (!spendResult.applied()) {
                return denied("该消息已经处理过");
            }
            reserved = true;
        }
        return CommandCostReservation.approved(
                reserved, reserved ? costKey : null, cost.rewardPoints(), rewardKey);
    }

    /**
     * 插件业务成功后提交奖励，并在发送前生成包含结算后余额的最终响应。
     */
    @Override
    @Transactional
    public CommandResult commit(BotMessageContext context,
                                CommandDescriptor descriptor,
                                CommandCostReservation reservation,
                                CommandResult result) {
        if (ObjectUtil.isNull(reservation) || !reservation.rewarding()) {
            return result;
        }
        BotGroupCommandContext groupContext = groupContextResolver.resolve(context).orElse(null);
        if (ObjectUtil.isNull(groupContext)) {
            throw new IllegalStateException("命令执行完成，但逻辑群组已不可用");
        }
        BotGroupMember member = memberIdentityResolver
                .findOrCreateForCommand(groupContext.group().getId(), context)
                .orElseThrow(() -> new IllegalStateException(
                        "命令执行完成，但群组成员已不存在"));
        PointChangeResult change = pointAccountService.earn(
                commandPointPolicy.rewardOperation(
                groupContext.group().getId(),
                member.getId(),
                descriptor,
                reservation.rewardPoints(),
                reservation.rewardReference()));
        return commandPointPolicy.completeReward(descriptor, result, change);
    }

    @Override
    @Transactional
    public void refund(CommandCostReservation reservation, Throwable cause) {
        if (ObjectUtil.isNull(reservation) || !reservation.reserved()) {
            return;
        }
        pointAccountService.refund(reservation.reference(), "命令执行失败退款");
    }

    private CommandCostReservation denied(String reason) {
        return CommandCostReservation.denied(reason);
    }

    private boolean exists(String idempotencyKey) {
        return pointAccountService.existsByIdempotencyKey(idempotencyKey);
    }

}
