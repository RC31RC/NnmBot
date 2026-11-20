package com.nnm.bot.admin.service.bot.channel;

import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import java.util.List;

/** 汇总渠道连接状态，并说明状态来源是否跨实例共享。 */
public interface BotChannelStatusService {

    /**
     * 获取当前可见的渠道实例状态快照。
     *
     * @return 渠道状态快照
     */
    List<BotChannelSnapshot> snapshots();

    /**
     * 判断状态是否来自分布式共享存储。
     *
     * @return 状态共享时为 {@code true}
     */
    default boolean isDistributed() {
        return false;
    }
}
