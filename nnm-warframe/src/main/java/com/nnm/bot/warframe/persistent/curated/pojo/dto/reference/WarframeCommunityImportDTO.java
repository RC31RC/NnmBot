package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import java.util.List;
import java.util.Map;

/** 人工资料解析结果；无效条数包含超出明细上限的记录。 */
public record WarframeCommunityImportDTO(Map<String, WarframeCommunityInputDTO> records, int inputCount,
    int invalidCount, List<WarframeCommunityImportIssueDTO> issues) {
}
