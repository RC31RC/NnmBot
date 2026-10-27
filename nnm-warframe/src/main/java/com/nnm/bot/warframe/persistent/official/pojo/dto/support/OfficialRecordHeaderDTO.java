package com.nnm.bot.warframe.persistent.official.pojo.dto.support;

/** 从类型化输入提取的公共检索字段，不覆盖源字段原值。 */
public record OfficialRecordHeaderDTO(String officialName, String localizedName, String parentName,
                                   String productCategory, String description) {
}
