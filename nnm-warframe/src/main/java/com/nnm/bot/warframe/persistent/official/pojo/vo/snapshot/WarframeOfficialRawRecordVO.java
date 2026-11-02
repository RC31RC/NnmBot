package com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot;

/** 单条记录的已采纳和候选快照原文。 */
public record WarframeOfficialRawRecordVO(String previousJson, String incomingJson, String previousLocalizedJson,
    String incomingLocalizedJson) {
}
