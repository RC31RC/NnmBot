package com.nnm.bot.core.group.event;

/** 群组删除事务内同步通知各业务模块清理群组私有数据。 */
public record BotGroupDeletingEvent(long groupId) {
}
