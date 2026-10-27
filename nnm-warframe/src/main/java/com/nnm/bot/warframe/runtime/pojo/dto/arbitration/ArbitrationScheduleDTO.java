package com.nnm.bot.warframe.runtime.pojo.dto.arbitration;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

/**
 * 供仲裁命令和模板消费的查询结果；提示随本次查询重新计算，不随窗口缓存冻结。
 *
 * @param entries 按开始时刻排序并应用视图数量上限的已采纳排期
 * @param notice 第三方预测、数据陈旧和时段缺口说明
 * @param fetchedAt 查询窗口内最早的存档更新时间；当前采纳入口记录采纳时刻，空窗口为 null
 * @param zone 自然日筛选和展示采用的配置时区
 */
public record ArbitrationScheduleDTO(
        List<ArbitrationScheduleEntryDTO> entries, String notice, Instant fetchedAt, ZoneId zone) {
    /**
     * 防止调用者修改结果列表并影响复用该投影的其他查询。
     *
     * @param entries 条目集合。
     * @param notice 提示。
     * @param fetchedAt 拉取时间时刻。
     * @param zone 时区。
     */
    public ArbitrationScheduleDTO {
        entries = List.copyOf(entries);
    }
}
