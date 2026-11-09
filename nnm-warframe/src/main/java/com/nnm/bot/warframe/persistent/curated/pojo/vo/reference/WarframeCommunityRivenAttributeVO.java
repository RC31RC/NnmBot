package com.nnm.bot.warframe.persistent.curated.pojo.vo.reference;

import java.util.Set;

/** 社区别名编辑可选择的官方裂罅属性，不包含搜索占位项。 */
public record WarframeCommunityRivenAttributeVO(String code, String label, String effectEn, Boolean negativeOnly,
    Set<String> exclusiveTo) {
}
