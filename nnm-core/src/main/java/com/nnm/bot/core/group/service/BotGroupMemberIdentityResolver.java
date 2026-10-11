package com.nnm.bot.core.group.service;

import java.time.LocalDateTime;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 将渠道消息身份解析为逻辑群组成员。
 *
 * <p>逻辑成员只有一条记录，渠道差异由各自身份列承载，避免同一用户因渠道不同
 * 被拆成多份积分和角色数据。</p>
 */
@Component
public class BotGroupMemberIdentityResolver {

    private final BotGroupMemberRepository memberRepository;
    private final BotGroupCommandContextResolver groupContextResolver;

    public BotGroupMemberIdentityResolver(
            BotGroupMemberRepository memberRepository,
            BotGroupCommandContextResolver groupContextResolver) {
        this.memberRepository = memberRepository;
        this.groupContextResolver = groupContextResolver;
    }

    public Optional<BotGroupMember> findByContext(
            Long groupId, BotMessageContext context) {
        return this.findByPlatformIdentity(groupId, context.platform(), context.senderId());
    }

    public Optional<BotGroupMember> findByPlatformIdentity(
            Long groupId, BotPlatform platform, String identity) {
        return switch (platform) {
            case ONEBOT -> this.memberRepository.findByGroupIdAndQqId(groupId, identity);
            case QQ_OFFICIAL ->
                    this.memberRepository.findByGroupIdAndMemberOpenid(groupId, identity);
            default -> this.memberRepository.findByGroupIdAndKookId(groupId, identity);
        };
    }

    /**
     * OneBot 可以直接确认 QQ 身份，因此在已解析到逻辑群组时首次发言即建档。
     * QQ 官方只提供 OpenID，仍由绑定流程显式建立群组成员关系。
     */
    public Optional<BotGroupMember> findOrCreateForCommand(
            Long groupId, BotMessageContext context) {
        Optional<BotGroupMember> existing = findByContext(groupId, context);
        if (existing.isPresent() || context.platform() != BotPlatform.ONEBOT) {
            return existing;
        }
        BotGroupMember member = new BotGroupMember();
        member.setGroupId(groupId);
        member.setQqId(context.senderId());
        member.setQqIdVerified(true);
        member.setQqBoundAt(LocalDateTime.now());
        member.setMemberRole("MEMBER");
        member.setEnabled(true);
        return Optional.of(memberRepository.save(member));
    }

    /**
     * 为所有命令入口解析当前逻辑群组。只有 OneBot 且存在启用绑定时才自动建档，
     * QQ 官方仍必须通过显式绑定建立群组成员关系。
     */
    public Optional<BotGroupMember> findOrCreateForCommand(BotMessageContext context) {
        if (context == null || !context.isGroupMessage()) {
            return Optional.empty();
        }
        return groupContextResolver.resolve(context)
                .flatMap(groupContext -> findOrCreateForCommand(
                        groupContext.group().getId(), context));
    }
}
