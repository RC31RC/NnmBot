package com.nnm.bot.core.group.port;

import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class OneBotMemberIdentityAdapter implements BotGroupMemberIdentityAdapter {

    @Override
    public BotPlatform platform() {
        return BotPlatform.ONEBOT;
    }

    @Override
    public Optional<BotGroupMember> find(
            BotGroupMemberRepository repository, long groupId, String platformUserId) {
        return repository.findByGroupIdAndQqId(groupId, platformUserId);
    }

    @Override
    public String identity(BotGroupMember member) {
        return member.getQqId();
    }

    @Override
    public void assign(BotGroupMember member, String platformUserId) {
        member.setQqId(platformUserId);
        member.setQqIdVerified(true);
        if (member.getQqBoundAt() == null) member.setQqBoundAt(LocalDateTime.now());
    }

    @Override
    public void clear(BotGroupMember member) {
        member.setQqId(null);
        member.setQqIdVerified(false);
        member.setQqBoundAt(null);
    }
}
