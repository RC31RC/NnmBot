package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;

/** 单个官方文件的类型化记录摘要及独立保留的审核原文。 */
public record OfficialImportRecordDTO(String path, WarframeOfficialDataDomain domain, String recordKey,
        String officialName, String localizedName, String parentName, String productCategory,
        String description, String rawJson, String localizedJson) {
    public String identity() {
        return domain.domainCode() + ":" + recordKey;
    }
}
