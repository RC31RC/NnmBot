package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

/** ref 为空时先远程发现最新 tag；非空时只允许日期 tag 或完整 commit。 */
public record WarframeOfficialRefreshDTO(String ref, String productPath) {
}
