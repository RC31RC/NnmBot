package com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades;

/** 升级类命令切分后的检索词、等级和页码。 */
public record WarframeUpgradeCommandDTO(String keyword, Integer rank, int page, String error) {
}
