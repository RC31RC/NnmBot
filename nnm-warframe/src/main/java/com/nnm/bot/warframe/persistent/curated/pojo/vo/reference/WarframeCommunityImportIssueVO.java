package com.nnm.bot.warframe.persistent.curated.pojo.vo.reference;

/** 管理端用于定位和修正人工资料的导入错误。 */
public record WarframeCommunityImportIssueVO(int recordNumber, String key, String field, String message) {
}
