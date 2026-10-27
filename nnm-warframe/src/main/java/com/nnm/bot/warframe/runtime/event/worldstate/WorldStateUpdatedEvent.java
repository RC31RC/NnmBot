package com.nnm.bot.warframe.runtime.event.worldstate;

import cn.hutool.core.util.ObjectUtil;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import java.time.Instant;

/**
 * 发布给通知、管理端和指标适配器的稳定世界状态事件。
 *
 * @param previous 更新前的世界状态；首次加载时可能为空
 * @param current 更新后的世界状态
 * @param observedAt 本地观察到本次更新的时间
 */
public record WorldStateUpdatedEvent(
        WorldStateDTO previous,
        WorldStateDTO current,
        Instant observedAt
) {
    /**
     * 构造世界状态更新时间事件，并保存初始化参数。
     *
     * @param previous 上一。
     * @param current 当前世界状态快照。
     * @param observedAt 实际观测时刻。
     */
    public WorldStateUpdatedEvent {
        observedAt = ObjectUtil.isNull(observedAt) ? Instant.now() : observedAt;
    }
}
