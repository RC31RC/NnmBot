package com.nnm.bot.warframe.persistent.curated.pojo.vo.reference;

import java.time.Instant;

/** 社区数据页的单条记录及其所属分区的业务字段。 */
public record WarframeCommunityRecordVO<T>(Long id, String key, T value, Instant updatedAt, long version) {
}
