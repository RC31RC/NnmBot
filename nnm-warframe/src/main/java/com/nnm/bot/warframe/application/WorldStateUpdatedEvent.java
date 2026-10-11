package com.nnm.bot.warframe.application;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.model.WorldState;

import java.time.Instant;
import java.util.List;

/**
 * 发布给通知、管理端和指标适配器的稳定世界状态事件。
 *
 * @param previous 更新前的世界状态；首次加载时可能为空
 * @param current 更新后的世界状态
 * @param changedSections 发生变化的世界状态分区编码
 * @param observedAt 本地观察到本次更新的时间
 */
public record WorldStateUpdatedEvent(
        WorldState previous,
        WorldState current,
        List<String> changedSections,
        Instant observedAt
) {
    public WorldStateUpdatedEvent {
        changedSections = CollUtil.isEmpty(changedSections)
                ? List.of()
                : List.copyOf(changedSections);
        observedAt = ObjectUtil.isNull(observedAt) ? Instant.now() : observedAt;
    }
}
