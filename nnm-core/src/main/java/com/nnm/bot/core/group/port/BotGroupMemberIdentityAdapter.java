package com.nnm.bot.core.group.port;

import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import java.util.Optional;

/** Maps one platform identity onto the logical-group member record. */
public interface BotGroupMemberIdentityAdapter {

    BotPlatform platform();

    Optional<BotGroupMember> find(
            BotGroupMemberRepository repository, long groupId, String platformUserId);

    String identity(BotGroupMember member);

    void assign(BotGroupMember member, String platformUserId);

    void clear(BotGroupMember member);
}
