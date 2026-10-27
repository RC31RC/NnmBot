package com.nnm.bot.core.mq.messagedescriptor.notification;

import com.nnm.bot.core.mq.consumerdescriptor.notification.GroupNotificationConsumerDescriptor;
import com.nnm.bot.core.mq.message.notification.GroupNotificationRequest;
import io.github.rdt.queue.ConsumerDescriptor;
import io.github.rdt.queue.MessageDescriptor;
import io.github.rdt.queue.QueueMode;

/** 主题与投递模式不依赖消息载荷或远端元数据。 */
public abstract class GroupNotificationMessageDescriptor extends MessageDescriptor<GroupNotificationRequest> {
    public static final String TOPIC = "bot-group-notifications";

    protected GroupNotificationMessageDescriptor() {
    }

    protected GroupNotificationMessageDescriptor(GroupNotificationRequest payload) {
        super(payload);
    }

    @Override
    public String topic() {
        return TOPIC;
    }

    @Override
    public QueueMode mode() {
        return QueueMode.RELIABLE_BROADCAST;
    }

    @Override
    public ConsumerDescriptor consumer(String group) {
        return new GroupNotificationConsumerDescriptor();
    }
}
