package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

/** 一次审批下单份 JSON 的分类提交状态。 */
public record WarframeOfficialApplyFileDTO(String path, String status, boolean addedDone, boolean changedDone,
    boolean removedDone, int attempts, String error) {
}
