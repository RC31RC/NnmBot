package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;

/** 单个官方文件的类型化记录摘要及独立保留的审核原文。 */
public record OfficialImportRecordDTO(String path, WarframeOfficialDataDomain domain, String recordKey,
    String officialName, String localizedName, String parentName, String productCategory, String description,
    String rawJson, String localizedJson) {
    /**
     * 生成当前记录在所属官方数据领域内的稳定身份。
     *
     * @return 由领域编码和记录业务键组成的身份键。
     */
    public String identity() {
        return domain.domainCode() + ":" + recordKey;
    }
}
