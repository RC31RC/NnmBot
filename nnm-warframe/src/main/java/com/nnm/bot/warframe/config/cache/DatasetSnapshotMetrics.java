package com.nnm.bot.warframe.config.cache;

/** 大快照显式报告内部记录数，避免把一个缓存条目误认为一条业务记录。 */
public interface DatasetSnapshotMetrics {
    /** @return 本快照拥有的业务记录数，不包含索引入口的重复引用。 */
    long entryCount();
}
