package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

/** tag 的展示名称及解析后的不可变提交；下载阶段只使用 commit。 */
public record WarframeOfficialVersionDTO(String ref, String commit) {
}
