package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

/** 外部字段到已知目标的社区映射读取投影。 */
public interface WarframeCommunityReferenceMappingDTO extends WarframeCommunityRowDTO {
    String getMappingScope();

    String getSourceKey();

    String getTargetKey();
}
