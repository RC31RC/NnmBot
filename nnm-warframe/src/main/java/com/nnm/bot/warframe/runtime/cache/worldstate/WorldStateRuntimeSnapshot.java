package com.nnm.bot.warframe.runtime.cache.worldstate;

import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData;
import java.time.Instant;
import java.util.List;

/**
 * 当前世界状态的原子运行时快照。
 *
 * <p>领域投影和原始响应必须一起替换，避免解析结果与排障数据来自不同
 * 的上游响应。本地原子引用服务高频读取，不保存历史快照表。</p>
 *
 * @param rawData Jackson 绑定的官方原始对象，非官方输入时为空
 * @param state 经映射和业务补全后的不可变运行时 DTO
 * @param source 本次快照的数据来源
 * @param rawJson 上游完整原文，供字段变更和绑定问题排查
 * @param contentHash 原文的 SHA-256 摘要
 * @param fetchedAt 本地获取数据的时间
 * @param topLevelFields 上游响应实际携带的顶层字段目录
 */
public record WorldStateRuntimeSnapshot(
        OfficialWorldStateRawData rawData,
        WorldStateDTO state,
        String source,
        String rawJson,
        String contentHash,
        Instant fetchedAt,
        List<String> topLevelFields) {

    /**
     * 固化顶层字段目录，防止调用方修改快照元数据。
     *
     * @param rawData Jackson 绑定得到的原始协议对象。
     * @param state 已经解析的当前世界状态。
     * @param source 当前数据来源标识。
     * @param rawJson 用于审核或排障的原始响应文本。
     * @param contentHash 内容哈希。
     * @param fetchedAt 拉取时间时刻。
     * @param topLevelFields 最高等级字段集合。
     */
    public WorldStateRuntimeSnapshot {
        topLevelFields = topLevelFields == null ? List.of() : List.copyOf(topLevelFields);
    }

    /**
     * 从已有运行时 DTO 构造不含官方原文的快照。
     *
     * @param state 已经解析的当前世界状态。
     * @return 世界状态运行时快照对象。
     */
    public static WorldStateRuntimeSnapshot fromState(WorldStateDTO state) {
        return new WorldStateRuntimeSnapshot(
                null,
                state,
                state == null || state.metadata() == null ? null : state.metadata().source(),
                null,
                state == null || state.metadata() == null
                        ? null : state.metadata().contentDigest(),
                state == null || state.metadata() == null ? null : state.metadata().fetchedAt(),
                List.of());
    }
}
