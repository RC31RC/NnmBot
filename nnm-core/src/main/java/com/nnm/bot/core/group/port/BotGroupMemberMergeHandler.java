package com.nnm.bot.core.group.port;

/** Rewrites a business module's member references while two logical members are merged. */
public interface BotGroupMemberMergeHandler {

    void mergeReferences(long groupId, long sourceMemberId, long targetMemberId);
}
