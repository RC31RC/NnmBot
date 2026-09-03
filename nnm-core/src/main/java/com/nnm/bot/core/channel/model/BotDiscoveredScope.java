package com.nnm.bot.core.channel.model;

import cn.hutool.core.collection.CollUtil;

import java.util.List;

/**
 * 渠道发现到的群组或其他作用域，不代表数据库中已经存在该作用域。
 *
 * @param scopeType 作用域类型
 * @param scopeKey 平台侧作用域标识
 * @param displayName 作用域显示名称
 * @param memberCount 平台返回的成员数量
 * @param members 平台返回的成员列表
 * @param membersComplete 成员列表是否是平台确认完整的全量结果
 */
public record BotDiscoveredScope(
        String scopeType,
        String scopeKey,
        String displayName,
        Integer memberCount,
        List<BotDiscoveredMember> members,
        boolean membersComplete) {

    /**
     * 保留原有构造方式。历史调用方传入成员列表时，默认视为完整列表。
     */
    public BotDiscoveredScope(
            String scopeType,
            String scopeKey,
            String displayName,
            Integer memberCount,
            List<BotDiscoveredMember> members) {
        this(scopeType, scopeKey, displayName, memberCount, members, true);
    }

    public BotDiscoveredScope {
        members = CollUtil.isEmpty(members) ? List.of() : List.copyOf(members);
    }
}
