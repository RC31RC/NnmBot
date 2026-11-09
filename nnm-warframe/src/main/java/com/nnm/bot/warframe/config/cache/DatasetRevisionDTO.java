package com.nnm.bot.warframe.config.cache;

/** 数据集缓存的持久化版本通知；不携带业务记录或跨表派生结果。 */
public record DatasetRevisionDTO(String dataset, long revision) {
}
