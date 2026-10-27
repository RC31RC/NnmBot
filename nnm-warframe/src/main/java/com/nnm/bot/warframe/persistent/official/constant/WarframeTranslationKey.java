package com.nnm.bot.warframe.persistent.official.constant;

import java.util.Locale;

public final class WarframeTranslationKey {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeTranslationKey() {
    }

    /**
     * 根据英文名称生成官方名称翻译的稳定查找键。
     * @param englishName 英文名称。
     * @return 优先使用的业务名称对应的文本。
     */
    public static String name(String englishName) {
        return "name:" + englishName.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
