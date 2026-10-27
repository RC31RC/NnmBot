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
import com.nnm.bot.core.group.service.BotGroupMemberIdentityAdapters;
import com.nnm.bot.core.group.service.BotGroupMemberMergeService;
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
    private BotGroupMemberIdentityAdapters identityAdapters = BotGroupMemberIdentityAdapters.defaults();
    @Resource
    private PointAccountService pointAccountService;
    @Resource
    private BotGroupMemberMergeService memberMergeService;

    @Override
    @Transactional
    public CommandResult signIn(BotMessageContext context) {
        BotGroupCommandContext groupContext = resolveGroup(context);
        if (ObjectUtil.isNull(groupContext)) {
            return CommandResult.text("签到只能在已配置的群组中使用");
        }
        BotGroupMember member = resolveWritableMember(groupContext, context);
        if (ObjectUtil.isNull(member)) {
            return CommandResult.text("当前用户还未加入该逻辑群组");
        }

        PointBalance balance = pointAccountService.balance(groupContext.group().getId(), member.getId())
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
        BotGroupMember member = resolveExistingMember(groupContext, context);
        if (ObjectUtil.isNull(member)) {
            return CommandResult.text("当前用户还未加入该逻辑群组");
        }
        PointBalance balance = pointAccountService.balance(
                        groupContext.group().getId(), member.getId())
                .orElseThrow(() -> new IllegalStateException("积分账户不存在"));
        return result("当前积分：" + balance.points(), balance);
    }

    private BotGroupMember resolveWritableMember(
            BotGroupCommandContext groupContext, BotMessageContext context) {
        return memberIdentityResolver
                .findOrCreateForWrite(groupContext.group().getId(), context)
                .filter(BotGroupMember::isEnabled)
                .orElse(null);
    }

    private BotGroupMember resolveExistingMember(
            BotGroupCommandContext groupContext, BotMessageContext context) {
        return memberIdentityResolver
                .findByContext(groupContext.group().getId(), context)
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

        BotGroupMember member;
        if (context.platform() == BotPlatform.ONEBOT) {
            member = memberRepository.findByGroupIdAndQqId(groupContext.group().getId(), qqUserId)
                    .orElseGet(() -> newMember(groupContext.group(), context));
            identityAdapters.assign(BotPlatform.ONEBOT, member, qqUserId);
            member.setQqIdVerified(true);
            member.setQqBoundAt(LocalDateTime.now());
            memberRepository.save(member);
            if (!member.isEnabled()) {
                return CommandResult.text("当前群组成员身份已停用，请联系管理员");
            }
            return CommandResult.text("绑定成功，QQ 身份已记录；需要跨渠道关联时请通过官方机器人发送绑定命令");
        }

        BotGroupMember byQqId = memberRepository
                .findByGroupIdAndQqId(groupContext.group().getId(), qqUserId)
                .orElse(null);
        BotGroupMember byOpenId = memberRepository
                .findByGroupIdAndMemberOpenid(groupContext.group().getId(), context.senderId())
                .orElse(null);
        if (byQqId != null && byOpenId != null && !byQqId.getId().equals(byOpenId.getId())) {
            ensureMergeCompatible(byQqId, byOpenId, qqUserId, context.senderId());
            if (memberMergeService == null) {
                throw new IllegalStateException("成员合并服务未装配");
            }
            member = memberMergeService.merge(groupContext.group().getId(), byQqId, byOpenId);
        } else if (byQqId != null) {
            member = byQqId;
        } else if (byOpenId != null) {
            member = byOpenId;
        } else {
            member = newMember(groupContext.group(), context);
        }
        if (!member.isEnabled()) {
            return CommandResult.text("当前群组成员身份已停用，请联系管理员");
        }
        if (StrUtil.isNotBlank(member.getQqId()) && !qqUserId.equals(member.getQqId())) {
            return CommandResult.text("当前平台身份已经绑定其他 QQ 号，不能直接覆盖");
        }
        if (StrUtil.isNotBlank(member.getMemberOpenid())
                && !context.senderId().equals(member.getMemberOpenid())) {
            return CommandResult.text("该 QQ 号已经绑定其他 QQ 官方成员身份");
        }
        member.setQqId(qqUserId);
        member.setQqBoundAt(member.getQqBoundAt() == null
                ? LocalDateTime.now() : member.getQqBoundAt());
        identityAdapters.assign(BotPlatform.QQ_OFFICIAL, member, context.senderId());
        // 官方自报 QQ 不改变既有可信核验状态，也不伪造新的核验标记。
        memberRepository.save(member);
        return CommandResult.text("绑定成功，QQ 官方身份已关联 QQ：" + qqUserId);
    }

    private void ensureMergeCompatible(BotGroupMember byQqId,
                                        BotGroupMember byOpenId,
                                        String qqUserId,
                                        String memberOpenid) {
        if (StrUtil.isNotBlank(byQqId.getMemberOpenid())
                && !memberOpenid.equals(byQqId.getMemberOpenid())) {
            throw new IllegalArgumentException("QQ号已经绑定其他 QQ 官方成员身份");
        }
        if (StrUtil.isNotBlank(byOpenId.getQqId())
                && !qqUserId.equals(byOpenId.getQqId())) {
            throw new IllegalArgumentException("当前平台身份已经绑定其他 QQ 号");
        }
        if (StrUtil.isNotBlank(byQqId.getKookId()) && StrUtil.isNotBlank(byOpenId.getKookId())
                && !byQqId.getKookId().equals(byOpenId.getKookId())) {
            throw new IllegalArgumentException("成员已有冲突的其他平台身份，不能自动合并");
        }
    }

    private BotGroupCommandContext resolveGroup(BotMessageContext context) {
        return groupContextResolver.resolve(context).orElse(null);
    }

    private BotGroupMember newMember(BotGroup group, BotMessageContext context) {
        BotGroupMember member = new BotGroupMember();
        member.setGroupId(group.getId());
        identityAdapters.assign(context.platform(), member, context.senderId());
        member.setDisplayName(null);
        member.setMemberRole("MEMBER");
        member.setEnabled(true);
        return member;
    }

    private CommandResult result(String text, PointBalance balance) {
        return CommandResult.view("system.points", text,
                new PointCommandDisplay(balance.points(), balance.totalEarned(), balance.totalSpent()));
    }

}
