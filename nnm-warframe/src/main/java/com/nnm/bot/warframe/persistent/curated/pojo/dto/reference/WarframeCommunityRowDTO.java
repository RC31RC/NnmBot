package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import java.time.Instant;

public interface WarframeCommunityRowDTO {
    /**
     * 读取标识。
     *
     * @return 本地记录主键。
     */
    Long getId();
    /**
     * 读取记录键。
     *
     * @return 记录键对应的文本。
     */
    String getRecordKey();
    /**
     * 读取更新时间时刻。
     *
     * @return 时间点对象。
     */
    Instant getUpdatedAt();
    /**
     * 读取版本。
     *
     * @return 当前操作计算或读取的数值。
     */
    long getVersion();
}
