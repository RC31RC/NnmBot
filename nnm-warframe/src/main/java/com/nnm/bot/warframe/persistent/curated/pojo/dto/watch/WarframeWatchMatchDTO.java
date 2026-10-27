package com.nnm.bot.warframe.persistent.curated.pojo.dto.watch;

import java.time.Instant;

/** 完整轮次的事件身份与结束时间；不把倒计时、售出数量或任务进度纳入身份。 */
public record WarframeWatchMatchDTO(String key, Instant expiry) {
}
