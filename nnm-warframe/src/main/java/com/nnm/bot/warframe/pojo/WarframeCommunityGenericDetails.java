package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.Map;

/** 结构不同的参考数据集使用原始字段展示，不套用其他数据集的实体模型。 */
public record WarframeCommunityGenericDetails(Map<String, Object> values)
        implements WarframeCommunityDatasetDetails {
    @JsonAnyGetter
    public Map<String, Object> values() {
        return values;
    }
}
