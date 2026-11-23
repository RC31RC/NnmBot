package com.nnm.bot.warframe.persistent.curated.pojo.vo.reference;

/** 社区数据页中的资料分区及可用操作。 */
public record WarframeCommunityDatasetVO(String code, String label, String description, long recordCount,
    boolean editable) {
}
