package com.nnm.bot.core.channel.model;

import java.util.List;

/** Result of querying members for a known platform scope. */
public record BotMemberQueryResult(List<BotDiscoveredMember> members, boolean complete) {

    public BotMemberQueryResult {
        members = members == null ? List.of() : List.copyOf(members);
    }
}
