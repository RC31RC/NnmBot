package com.nnm.bot.warframe.runtime.pojo.vo.arbitration;

import com.nnm.bot.common.response.PageResult;
import java.time.Instant;

/** 仲裁快照采集信息和分页结果；不受机器人预览条数限制。 */
public record WarframeArbitrationManagementVO(boolean available, boolean stale, String source, Instant fetchedAt,
    Instant rangeFrom, Instant rangeUntil, PageResult<EntryVO> entries, String notice) {

    /** 已解析排期的展示行，保留来源引用与边界信息供详情查询。 */
    public record EntryVO(String id, Instant activation, Instant expiry, String node, String missionType, String enemy,
        String planet, String boundsJson, String nodeReference) {
    }
}
