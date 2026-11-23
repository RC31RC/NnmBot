package com.nnm.bot.warframe.runtime.cache.worldstate;

import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData;
import java.time.Instant;

/**
 * 当前世界状态的原子运行时快照。
 *
 * <p>
 * 类型化协议输入、领域投影和摘要元数据一起替换，避免解析结果来自不同的上游响应。本地原子引用服务高频读取，不保存历史快照表。
 * </p>
 *
 * @param rawData Jackson 绑定的官方原始对象，非官方输入时为空
 * @param state 经映射和业务补全后的不可变运行时 DTO
 * @param source 本次快照的数据来源
 * @param contentHash 原文的 SHA-256 摘要
 * @param fetchedAt 本地获取数据的时间
 */
public record WorldStateRuntimeSnapshot(OfficialWorldStateRawData rawData, WorldStateDTO state, String source,
    String contentHash, Instant fetchedAt) {

    /**
     * 从已有运行时 DTO 构造不含官方原文的快照。
     *
     * @param state 已经解析的当前世界状态。
     * @return 世界状态运行时快照对象。
     */
    public static WorldStateRuntimeSnapshot fromState(WorldStateDTO state) {
        return new WorldStateRuntimeSnapshot(null, state,
            state == null || state.metadata() == null ? null : state.metadata().source(),
            state == null || state.metadata() == null ? null : state.metadata().contentDigest(),
            state == null || state.metadata() == null ? null : state.metadata().fetchedAt());
    }
}
