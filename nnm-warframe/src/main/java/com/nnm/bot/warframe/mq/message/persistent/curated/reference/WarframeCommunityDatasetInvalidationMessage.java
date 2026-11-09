package com.nnm.bot.warframe.mq.message.persistent.curated.reference;

import com.nnm.bot.warframe.config.cache.DatasetRevisionDTO;
import com.nnm.bot.warframe.mq.messagedescriptor.persistent.curated.reference.WarframeCommunityDatasetInvalidationMessageDescriptor;

/** 跨实例传输消息；发布和消费策略由独立描述符定义。 */
public final class WarframeCommunityDatasetInvalidationMessage
    extends WarframeCommunityDatasetInvalidationMessageDescriptor {
    /**
     * 构造物品索引缓存失效消息，并保存初始化参数。
     */
    public WarframeCommunityDatasetInvalidationMessage() {}

    /**
     * 构造物品索引缓存失效消息，并保存初始化参数。
     *
     * @param payload 外部响应或消息的载荷。
     */
    public WarframeCommunityDatasetInvalidationMessage(DatasetRevisionDTO payload) {
        super(payload);
    }
}
