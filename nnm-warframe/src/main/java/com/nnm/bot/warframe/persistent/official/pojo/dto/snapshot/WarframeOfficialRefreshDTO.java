package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

import jakarta.validation.constraints.NotBlank;

/** 刷新单个官方 JSON；远程刷新使用 UTC 秒级 tag，本地模式使用配置的 local-ref；文件路径使用 .json.gz 原件路径。 */
public record WarframeOfficialRefreshDTO(String ref, @NotBlank String productPath) {
}
