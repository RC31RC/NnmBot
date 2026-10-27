package com.nnm.bot.warframe.util;

import cn.hutool.core.util.StrUtil;
import java.text.Normalizer;
import java.util.Locale;

/** 名称输入规范化；物品检索和周紫卡代码簿保留各自的标点语义。 */
public final class WarframeNameNormalizer {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeNameNormalizer() {
    }

    /**
     * 统一物品查询名称的 Unicode、大小写和分隔符，生成稳定匹配键。
     * @param input 待解析或校验的输入。
     * @return 可用于物品检索的规范化名称键。
     */
    public static String item(String input) {
        if (StrUtil.isBlank(input)) {
            return "";
        }
        String normalized = Normalizer.normalize(input.trim(), Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT);
        return normalized.replaceAll("[\\s_\\-·•/\\\\]+", "");
    }

    /**
     * 按周紫卡代码簿的名称规则规范化武器名，保留该接口要求的标点语义。
     * @param value 待校验或规范化的文本。
     * @return 可用于周紫卡代码簿匹配的武器名称键。
     */
    public static String weeklyWeapon(String value) {
        return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFKC)
                .trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
