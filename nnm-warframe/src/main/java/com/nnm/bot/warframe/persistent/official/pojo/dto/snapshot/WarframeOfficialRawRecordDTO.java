package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

/** 按需读取的两个版本记录原文，不写入预览差异或业务表。 */
public record WarframeOfficialRawRecordDTO(String previousJson, String incomingJson, String previousLocalizedJson,
    String incomingLocalizedJson) {
}
