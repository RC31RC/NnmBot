package com.nnm.bot.core.group.port;

import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class QqOfficialMemberIdentityAdapter implements BotGroupMemberIdentityAdapter {

    @Override
    public BotPlatform platform() {
        return BotPlatform.QQ_OFFICIAL;
    }

    @Override
    public Optional<BotGroupMember> find(
            BotGroupMemberRepository repository, long groupId, String platformUserId) {
        return repository.findByGroupIdAndMemberOpenid(groupId, platformUserId);
    }

    @Override
    public String identity(BotGroupMember member) {
        return member.getMemberOpenid();
    }

    @Override
    public void assign(BotGroupMember member, String platformUserId) {
        member.setMemberOpenid(platformUserId);
    }

    @Override
    public void clear(BotGroupMember member) {
        member.setMemberOpenid(null);
    }
}
