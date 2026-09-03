package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.domain.model.WorldState;

import java.time.Instant;
import java.util.List;

/**
 * 当前世界状态的原子运行时快照。
 *
 * <p>领域投影和原始响应必须一起替换，避免解析结果与排障数据来自不同
 * 的上游响应。该对象只存在于内存，不是历史快照表。</p>
 */
public record WorldStateRuntimeSnapshot(
        WorldState state,
        String source,
        String rawJson,
        String contentHash,
        Instant fetchedAt,
        List<String> topLevelFields) {

    public WorldStateRuntimeSnapshot {
        topLevelFields = topLevelFields == null ? List.of() : List.copyOf(topLevelFields);
    }

    public static WorldStateRuntimeSnapshot fromState(WorldState state) {
        return new WorldStateRuntimeSnapshot(
                state,
                state == null || state.metadata() == null ? null : state.metadata().source(),
                null,
                state == null || state.metadata() == null
                        ? null : state.metadata().contentDigest(),
                state == null || state.metadata() == null ? null : state.metadata().fetchedAt(),
                List.of());
    }
}
