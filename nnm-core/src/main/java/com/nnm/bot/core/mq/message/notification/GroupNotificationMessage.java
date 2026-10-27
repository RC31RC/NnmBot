package com.nnm.bot.core.mq.message.notification;

import com.nnm.bot.core.mq.messagedescriptor.notification.GroupNotificationMessageDescriptor;

/** 跨实例传输消息；发布和消费策略由独立描述符定义。 */
public final class GroupNotificationMessage extends GroupNotificationMessageDescriptor {
    public GroupNotificationMessage() {
    }

    public GroupNotificationMessage(GroupNotificationRequest payload) {
        super(payload);
    }
}
