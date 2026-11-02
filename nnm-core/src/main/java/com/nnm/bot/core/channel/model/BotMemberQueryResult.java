package com.nnm.bot.core.channel.model;

import java.util.List;

/** 已知平台作用域的成员查询结果。 */
public record BotMemberQueryResult(List<BotDiscoveredMember> members, boolean complete) {

    public BotMemberQueryResult {
        members = members == null ? List.of() : List.copyOf(members);
    }
}
