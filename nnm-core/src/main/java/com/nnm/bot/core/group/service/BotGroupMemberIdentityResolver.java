package com.nnm.bot.core.group.service;

import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.message.model.BotMessageContext;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 将渠道消息身份解析为逻辑群组成员。 */
@Component
public class BotGroupMemberIdentityResolver {

    @Resource
    private BotGroupMemberRepository memberRepository;
    @Resource
    private BotGroupCommandContextResolver groupContextResolver;
    @Resource
    private BotGroupMemberIdentityAdapters identityAdapters = BotGroupMemberIdentityAdapters.defaults();
    @Resource
    private CanonicalMemberResolver canonicalMembers;

    public Optional<BotGroupMember> findByContext(
            Long groupId, BotMessageContext context) {
        if (context.execution() != null && context.execution().groupView() != null
                && context.execution().groupView().id() == groupId) {
            return Optional.ofNullable(context.execution().member()).map(member -> member.entity());
        }
        return this.findByPlatformIdentity(groupId, context.platform(), context.senderId());
    }

    public Optional<BotGroupMember> findByPlatformIdentity(
            Long groupId, BotPlatform platform, String identity) {
        return identityAdapters.find(memberRepository, groupId, platform, identity)
                .map(member -> resolveCanonical(groupId, member));
    }

    /**
     * 在真正要写入积分、签到或订阅等持久数据的边界幂等建立成员。
     * 普通命令入口不得调用此方法。
     */
    public Optional<BotGroupMember> findOrCreateForWrite(
            Long groupId, BotMessageContext context) {
        Optional<BotGroupMember> existing = findByPlatformIdentity(groupId, context.platform(), context.senderId());
        if (existing.isPresent()) {
            return existing;
        }
        BotGroupMember member = new BotGroupMember();
        member.setGroupId(groupId);
        identityAdapters.assign(context.platform(), member, context.senderId());
        if (context.platform() == BotPlatform.ONEBOT) {
            member.setQqIdVerified(true);
            member.setQqBoundAt(LocalDateTime.now());
        }
        member.setMemberRole("MEMBER");
        member.setEnabled(true);
        try {
            return Optional.of(memberRepository.save(member));
        } catch (RuntimeException exception) {
            // 唯一索引竞态下重读胜出的记录，避免同一平台身份产生两条成员。
            return Optional.of(findByPlatformIdentity(groupId, context.platform(), context.senderId()).orElseThrow(() -> exception));
        }
    }

    public Optional<BotGroupMember> findOrCreateForWrite(BotMessageContext context) {
        if (context == null || !context.isGroupMessage()) {
            return Optional.empty();
        }
        return groupContextResolver.resolve(context)
                .flatMap(groupContext -> findOrCreateForWrite(
                        groupContext.group().getId(), context));
    }

    public long canonicalMemberId(long groupId, long memberId) {
        return canonicalMembers.resolve(groupId, memberId);
    }

    private BotGroupMember resolveCanonical(long groupId, BotGroupMember member) {
        if (member.getId() == null) {
            return member;
        }
        long canonicalId = canonicalMemberId(groupId, member.getId());
        if (canonicalId == member.getId()) {
            return member;
        }
        return memberRepository.findById(canonicalId).filter(target -> target.getGroupId().equals(groupId))
                .orElseThrow(() -> new IllegalStateException("成员合并目标不存在或不属于当前群组"));
    }
}
