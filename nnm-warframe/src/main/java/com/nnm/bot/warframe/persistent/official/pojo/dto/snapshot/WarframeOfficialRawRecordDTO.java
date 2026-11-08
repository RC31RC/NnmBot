package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

/** 审核前按需读取候选版本记录原文；比对旧值来自数据库。 */
public record WarframeOfficialRawRecordDTO(String incomingJson, String incomingLocalizedJson) {
}
