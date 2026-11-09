package com.nnm.bot.warframe.mq.messagedescriptor.persistent.curated.reference;

import com.nnm.bot.warframe.config.cache.DatasetRevisionDTO;
import com.nnm.bot.warframe.mq.consumerdescriptor.persistent.curated.reference.WarframeCommunityDatasetInvalidationConsumerDescriptor;
import io.github.rdt.queue.ConsumerDescriptor;
import io.github.rdt.queue.MessageDescriptor;
import io.github.rdt.queue.QueueMode;

/** 主题与投递模式不依赖消息载荷或远端元数据。 */
public abstract class WarframeCommunityDatasetInvalidationMessageDescriptor
    extends MessageDescriptor<DatasetRevisionDTO> {
    public static final String TOPIC = "warframe-community-dataset-invalidated";

    /**
     * 构造物品索引缓存失效消息描述符，并保存初始化参数。
     */
    protected WarframeCommunityDatasetInvalidationMessageDescriptor() {}

    /**
     * 构造物品索引缓存失效消息描述符，并保存初始化参数。
     *
     * @param payload 外部响应或消息的载荷。
     */
    protected WarframeCommunityDatasetInvalidationMessageDescriptor(DatasetRevisionDTO payload) {
        super(payload);
    }

    /**
     * 取得跨实例消息使用的稳定主题。
     *
     * @return 跨实例消息使用的稳定主题对应的文本。
     */
    @Override
    public String topic() {
        return TOPIC;
    }

    /**
     * 取得当前操作使用的处理模式。
     *
     * @return 队列模式对象。
     */
    @Override
    public QueueMode mode() {
        return QueueMode.RELIABLE_BROADCAST;
    }

    /**
     * 取得当前业务的消息消费者定义。
     *
     * @param group 分组。
     * @return 消费者描述符对象。
     */
    @Override
    public ConsumerDescriptor consumer(String group) {
        return new WarframeCommunityDatasetInvalidationConsumerDescriptor();
    }
}
