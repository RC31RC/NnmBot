package com.nnm.bot.warframe.mq.message.persistent.official.dataset;

import com.nnm.bot.warframe.config.cache.DatasetRevisionDTO;
import com.nnm.bot.warframe.mq.messagedescriptor.persistent.official.dataset.WarframeOfficialDatasetInvalidationMessageDescriptor;

/** 跨实例传输消息；发布和消费策略由独立描述符定义。 */
public final class WarframeOfficialDatasetInvalidationMessage
    extends WarframeOfficialDatasetInvalidationMessageDescriptor {
    /**
     * 构造世界状态代码簿缓存失效消息，并保存初始化参数。
     */
    public WarframeOfficialDatasetInvalidationMessage() {}

    /**
     * 构造世界状态代码簿缓存失效消息，并保存初始化参数。
     *
     * @param payload 外部响应或消息的载荷。
     */
    public WarframeOfficialDatasetInvalidationMessage(DatasetRevisionDTO payload) {
        super(payload);
    }
}
