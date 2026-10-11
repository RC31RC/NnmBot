package com.nnm.bot.warframe.pojo;

import java.util.List;

/**
 * 人工维护物品及其别名、翻译。
 */
public record WarframeManualItemData(
        Long databaseId,
        String sourceKey,
        String officialName,
        String localizedName,
        String wikiSlug,
        String marketSlug,
        String remark,
        String rawJson,
        List<String> aliases,
        List<WarframeManualTranslationData> translations
) {

    public WarframeManualItemData {
        aliases = aliases == null ? List.of() : List.copyOf(aliases);
        translations = translations == null ? List.of() : List.copyOf(translations);
    }
}
