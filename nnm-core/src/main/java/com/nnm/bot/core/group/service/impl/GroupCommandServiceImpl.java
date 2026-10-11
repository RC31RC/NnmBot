package com.nnm.bot.core.group.service.impl;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.PointCommandDisplay;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.group.service.BotGroupCommandContext;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import com.nnm.bot.core.group.service.GroupCommandService;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.group.service.PointAccountService;
import com.nnm.bot.core.group.service.PointBalance;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 群组系统命令服务。平台身份是积分和幂等判断的依据，QQ 号绑定只是成员资料。
 */
@Service
public class GroupCommandServiceImpl implements GroupCommandService {

    @Resource
    private BotGroupCommandContextResolver groupContextResolver;
    @Resource
    private BotGroupMemberRepository memberRepository;
    @Resource
    private BotGroupMemberIdentityResolver memberIdentityResolver;
    @Resource
    private PointAccountService pointAccountService;

    @Override
    @Transactional
    public CommandResult signIn(BotMessageContext context) {
        BotGroupCommandContext groupContext = resolveGroup(context);
        if (ObjectUtil.isNull(groupContext)) {
            return CommandResult.text("签到只能在已配置的群组中使用");
        }
        BotGroupMember member = resolveMember(groupContext, context);
        if (ObjectUtil.isNull(member)) {
            return CommandResult.text("当前用户还未加入该逻辑群组");
        }

        PointBalance balance = pointAccountService.balance(
                        groupContext.group().getId(), member.getId())
                .orElseThrow(() -> new IllegalStateException("积分账户不存在"));
        // 签到奖励由命令积分守卫统一结算，这里只提供成员和当前账户快照。
        return result("当前积分：" + balance.points(), balance);
    }

    @Override
    @Transactional
    public CommandResult currentPoints(BotMessageContext context) {
        BotGroupCommandContext groupContext = resolveGroup(context);
        if (ObjectUtil.isNull(groupContext)) {
            return CommandResult.text("积分查询只能在已配置的群组中使用");
        }
        BotGroupMember member = resolveMember(groupContext, context);
        if (ObjectUtil.isNull(member)) {
            return CommandResult.text("当前用户还未加入该逻辑群组");
        }
        PointBalance balance = pointAccountService.balance(
                        groupContext.group().getId(), member.getId())
                .orElseThrow(() -> new IllegalStateException("积分账户不存在"));
        return result("当前积分：" + balance.points(), balance);
    }

    private BotGroupMember resolveMember(
            BotGroupCommandContext groupContext, BotMessageContext context) {
        return memberIdentityResolver
                .findOrCreateForCommand(groupContext.group().getId(), context)
                .filter(BotGroupMember::isEnabled)
                .orElse(null);
    }

    @Override
    @Transactional
    public CommandResult bindQq(BotMessageContext context, String qqUserId) {
        if (!context.isGroupMessage()) {
            return CommandResult.text("绑定命令只能在群聊中使用");
        }
        if (StrUtil.isBlank(qqUserId) || !qqUserId.trim().matches("\\d{5,20}")) {
            return CommandResult.text("QQ号格式不正确，请使用“绑定 QQ号”");
        }
        qqUserId = qqUserId.trim();
        if (context.platform() == BotPlatform.ONEBOT
                && !qqUserId.equals(context.senderId())) {
            return CommandResult.text("OneBot 只能绑定当前消息发送人的 QQ 号");
        }
        BotGroupCommandContext groupContext = resolveGroup(context);
        if (ObjectUtil.isNull(groupContext)) {
            return CommandResult.text("当前群聊尚未绑定逻辑群组");
        }

        BotGroupMember member = resolveBindingMember(groupContext.group(), context, qqUserId);
        if (!member.isEnabled()) {
            return CommandResult.text("当前群组成员身份已停用，请联系管理员");
        }
        if (member.isQqIdVerified() && !qqUserId.equals(member.getQqId())) {
            return CommandResult.text("当前平台身份已经绑定其他 QQ 号，不能直接覆盖");
        }

        boolean verified = context.platform() == BotPlatform.ONEBOT;
        if (context.platform() == BotPlatform.QQ_OFFICIAL
                && StrUtil.isNotBlank(member.getMemberOpenid())
                && !context.senderId().equals(member.getMemberOpenid())) {
            return CommandResult.text("该 QQ 号已经绑定其他 QQ 官方成员身份");
        }
        member.setQqId(qqUserId);
        member.setQqIdVerified(verified);
        member.setQqBoundAt(LocalDateTime.now());
        assignChannelIdentity(member, context);
        memberRepository.save(member);

        if (verified) {
            return CommandResult.text("绑定成功，当前群组成员已绑定 QQ：" + qqUserId);
        }
        return CommandResult.text("已记录 QQ：" + qqUserId
                + "。QQ 官方机器人只能确认当前用户 OpenID，无法从消息直接核验 QQ 号归属；"
                + "当前绑定标记为待人工确认，积分仍按本平台身份计算");
    }

    private BotGroupCommandContext resolveGroup(BotMessageContext context) {
        return groupContextResolver.resolve(context).orElse(null);
    }

    private BotGroupMember resolveBindingMember(
            BotGroup group, BotMessageContext context, String qqId) {
        if (context.platform() == BotPlatform.ONEBOT) {
            return memberIdentityResolver.findByContext(group.getId(), context)
                    .orElseGet(() -> newMember(group, context));
        }
        BotGroupMember byQqId = memberRepository
                .findByGroupIdAndQqId(group.getId(), qqId)
                .orElse(null);
        BotGroupMember byChannelId = memberIdentityResolver
                .findByContext(group.getId(), context)
                .orElse(null);
        if (ObjectUtil.isNotNull(byQqId) && ObjectUtil.isNotNull(byChannelId)
                && !byQqId.getId().equals(byChannelId.getId())) {
            throw new IllegalArgumentException("QQ号和当前平台身份已经分别绑定到不同群组成员");
        }
        if (ObjectUtil.isNotNull(byQqId)) {
            return byQqId;
        }
        if (ObjectUtil.isNotNull(byChannelId)) {
            return byChannelId;
        }
        return newMember(group, context);
    }

    private BotGroupMember newMember(BotGroup group, BotMessageContext context) {
        BotGroupMember member = new BotGroupMember();
        member.setGroupId(group.getId());
        assignChannelIdentity(member, context);
        member.setDisplayName(null);
        member.setMemberRole("MEMBER");
        member.setEnabled(true);
        return member;
    }

    private void assignChannelIdentity(
            BotGroupMember member, BotMessageContext context) {
        switch (context.platform()) {
            case ONEBOT -> member.setQqId(context.senderId());
            case QQ_OFFICIAL -> member.setMemberOpenid(context.senderId());
            default -> member.setKookId(context.senderId());
        }
    }

    private CommandResult result(String text, PointBalance balance) {
        return CommandResult.of(text,
                new PointCommandDisplay(balance.points(), balance.totalEarned(), balance.totalSpent()));
    }

}
