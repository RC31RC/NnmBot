package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

/** 前端选择的 tag 及其解析后的提交审计信息；下载阶段使用 ref。 */
public record WarframeOfficialVersionDTO(String ref, String commit) {
}
