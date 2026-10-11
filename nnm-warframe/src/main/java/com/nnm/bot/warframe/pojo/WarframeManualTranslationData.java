package com.nnm.bot.warframe.pojo;

/**
 * 人工维护物品的一条本地化翻译。
 */
public record WarframeManualTranslationData(
        Long databaseId,
        String locale,
        String translatedName,
        String remark
) {
}
