package com.nnm.bot.warframe.pojo;

/** 社区数据集分页查询参数。 */
public record WarframeCommunityDatasetQueryRequest(
        String keyword,
        Integer current,
        Integer size
) {
}
