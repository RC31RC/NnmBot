package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

/** 人工导入中一条记录的首个错误，记录序号从一开始。 */
public record WarframeCommunityImportIssueDTO(int recordNumber, String key, String field, String message) {
}
