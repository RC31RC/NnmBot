package com.nnm.bot.core.group.service;

import com.nnm.bot.core.group.context.BotGroupCommandContext;
import com.nnm.bot.core.group.constant.GroupPointConstants.TransactionType;
import com.nnm.bot.core.group.constant.GroupPointConstants.PointErrorCode;
import com.nnm.bot.common.error.BusinessException;
import cn.hutool.core.util.ObjectUtil;
import com.nnm.bot.core.messaging.model.BotMessageContext;
import com.nnm.bot.core.plugin.api.CommandCostConfigurationDTO;
import com.nnm.bot.core.plugin.api.CommandCostConfigurationProvider;
import com.nnm.bot.core.plugin.api.CommandCostGuard;
import com.nnm.bot.core.plugin.api.CommandCostReservationDTO;
import com.nnm.bot.core.plugin.api.CommandDescriptor;
import com.nnm.bot.core.plugin.api.CommandResult;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.pojo.dto.PointChangeResultDTO;
import com.nnm.bot.core.group.pojo.dto.PointOperationDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 群组命令积分守卫。扣费及奖励加入命令业务事务；已提交后的投递退款独立结算。
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
    public CommandCostReservationDTO reserve(BotMessageContext context, CommandDescriptor descriptor) {
        CommandCostConfigurationDTO cost = costConfigurationProvider.configuration(descriptor.code());
        if (ObjectUtil.isNull(cost) || !cost.active()) {
            return CommandCostReservationDTO.none();
        }
        if (!context.isGroupMessage()) {
            return denied("该命令只能在已配置的群组中使用");
        }

        BotGroupCommandContext groupContext = groupContextResolver.resolve(context).orElse(null);
        if (ObjectUtil.isNull(groupContext)) {
            return denied("当前群聊尚未绑定逻辑群组");
        }

        BotGroupMember member = memberIdentityResolver.findOrCreateForWrite(groupContext.group().getId(), context)
            .filter(BotGroupMember::isEnabled).orElse(null);
        if (ObjectUtil.isNull(member)) {
            return denied("当前用户尚未加入该逻辑群组");
        }

        String costKey = cost.chargeable()
            ? commandPointPolicy.costIdempotencyKey(context, descriptor, groupContext.group().getId(), member.getId())
            : null;
        String rewardKey = cost.rewardable()
            ? commandPointPolicy.rewardIdempotencyKey(context, descriptor, groupContext.group().getId(), member.getId())
            : null;
        boolean repeatedReward = exists(rewardKey);
        if (exists(costKey) || (repeatedReward && !commandPointPolicy.allowsRepeatedReward(descriptor))) {
            return denied("该消息已经处理过");
        }

        boolean skipRepeatedRewardCost = repeatedReward && commandPointPolicy.allowsRepeatedReward(descriptor);
        boolean reserved = false;
        if (cost.chargeable() && !skipRepeatedRewardCost) {
            PointChangeResultDTO spendResult =
                pointAccountService.spend(new PointOperationDTO(groupContext.group().getId(), member.getId(),
                    descriptor.code(), TransactionType.COMMAND_COST, cost.costPoints(), costKey, "命令积分消耗"));
            if (!spendResult.allowed()) {
                return denied(spendResult.message());
            }
            if (!spendResult.applied()) {
                return denied("该消息已经处理过");
            }
            reserved = true;
        }
        return CommandCostReservationDTO.approved(reserved, reserved ? costKey : null, cost.rewardPoints(), rewardKey);
    }

    /**
     * 插件业务成功后提交奖励，并在发送前生成包含结算后余额的最终响应。
     */
    @Override
    @Transactional
    public CommandResult commit(BotMessageContext context, CommandDescriptor descriptor,
        CommandCostReservationDTO reservation, CommandResult result) {
        if (ObjectUtil.isNull(reservation) || !reservation.rewarding()) {
            return result;
        }
        BotGroupCommandContext groupContext = groupContextResolver.resolve(context).orElse(null);
        if (ObjectUtil.isNull(groupContext)) {
            throw new BusinessException(PointErrorCode.GROUP_UNAVAILABLE_AFTER_COMMAND);
        }
        BotGroupMember member = memberIdentityResolver.findOrCreateForWrite(groupContext.group().getId(), context)
            .orElseThrow(() -> new BusinessException(PointErrorCode.MEMBER_UNAVAILABLE_AFTER_COMMAND));
        PointChangeResultDTO change =
            pointAccountService.earn(commandPointPolicy.rewardOperation(groupContext.group().getId(), member.getId(),
                descriptor, reservation.rewardPoints(), reservation.rewardReference()));
        return commandPointPolicy.completeReward(descriptor, result, change);
    }

    @Override
    @Transactional
    public void refund(CommandCostReservationDTO reservation, Throwable cause) {
        if (ObjectUtil.isNull(reservation) || !reservation.reserved()) {
            return;
        }
        pointAccountService.refund(reservation.reference(), "命令必要回复确定未发送退款");
    }

    private CommandCostReservationDTO denied(String reason) {
        return CommandCostReservationDTO.denied(reason);
    }

    private boolean exists(String idempotencyKey) {
        return pointAccountService.existsByIdempotencyKey(idempotencyKey);
    }

}
