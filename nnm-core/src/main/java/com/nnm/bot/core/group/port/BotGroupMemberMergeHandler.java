package com.nnm.bot.core.group.port;

/** 合并两个逻辑成员时，重写业务模块持有的成员引用。 */
public interface BotGroupMemberMergeHandler {

    void mergeReferences(long groupId, long sourceMemberId, long targetMemberId);
}
