package com.nnm.bot.core.group.service;

import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.port.BotGroupMemberIdentityAdapter;
import com.nnm.bot.core.group.port.OneBotMemberIdentityAdapter;
import com.nnm.bot.core.group.port.QqOfficialMemberIdentityAdapter;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Resolves channel-specific member identity behavior without platform switches in services. */
@Component
public class BotGroupMemberIdentityAdapters {

    private final Map<BotPlatform, BotGroupMemberIdentityAdapter> adapters;

    public BotGroupMemberIdentityAdapters(List<BotGroupMemberIdentityAdapter> adapters) {
        Map<BotPlatform, BotGroupMemberIdentityAdapter> indexed = new EnumMap<>(BotPlatform.class);
        for (BotGroupMemberIdentityAdapter adapter : adapters) {
            if (indexed.put(adapter.platform(), adapter) != null) {
                throw new IllegalArgumentException(
                        "Duplicate member identity adapter: " + adapter.platform());
            }
        }
        this.adapters = Map.copyOf(indexed);
    }

    public static BotGroupMemberIdentityAdapters defaults() {
        return new BotGroupMemberIdentityAdapters(List.of(
                new OneBotMemberIdentityAdapter(), new QqOfficialMemberIdentityAdapter()));
    }

    public Optional<BotGroupMember> find(
            BotGroupMemberRepository repository, long groupId,
            BotPlatform platform, String platformUserId) {
        return adapter(platform).find(repository, groupId, platformUserId);
    }

    public String identity(BotPlatform platform, BotGroupMember member) {
        return adapter(platform).identity(member);
    }

    public void assign(BotPlatform platform, BotGroupMember member, String platformUserId) {
        adapter(platform).assign(member, platformUserId);
    }

    public void clear(BotPlatform platform, BotGroupMember member) {
        adapter(platform).clear(member);
    }

    public boolean hasIdentity(BotPlatform platform, BotGroupMember member) {
        String identity = identity(platform, member);
        return identity != null && !identity.isBlank();
    }

    private BotGroupMemberIdentityAdapter adapter(BotPlatform platform) {
        BotGroupMemberIdentityAdapter adapter = adapters.get(platform);
        if (adapter == null) {
            throw new IllegalArgumentException("No member identity adapter for " + platform);
        }
        return adapter;
    }
}
