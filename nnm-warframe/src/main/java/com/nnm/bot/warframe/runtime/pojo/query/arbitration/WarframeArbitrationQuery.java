package com.nnm.bot.warframe.runtime.pojo.query.arbitration;

import java.time.Instant;

/** 仲裁管理查询；时间按任务区间相交筛选，不携带命令语法状态。 */
public record WarframeArbitrationQuery(Instant from, Instant until, String keyword, Integer current, Integer size) {
}
