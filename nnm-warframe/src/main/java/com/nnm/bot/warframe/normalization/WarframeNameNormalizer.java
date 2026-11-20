package com.nnm.bot.warframe.normalization;

import cn.hutool.core.text.CharSequenceUtil;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/** 名称输入规范化；物品检索和周紫卡代码簿保留各自的标点语义。 */
public final class WarframeNameNormalizer {
    private static final Pattern ARCHWING_DISPLAY_MARKER = Pattern.compile("^<ARCHWING>\\s*", Pattern.CASE_INSENSITIVE);

    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeNameNormalizer() {}

    /**
     * 统一物品查询名称的 Unicode、大小写和分隔符，生成稳定匹配键。
     *
     * @param input 待解析或校验的输入。
     * @return 可用于物品检索的规范化名称键。
     */
    public static String item(String input) {
        if (CharSequenceUtil.isBlank(input)) {
            return "";
        }
        String normalized =
            Normalizer.normalize(CharSequenceUtil.trim(input), Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
        return normalized.replaceAll("[\\s_\\-·•/\\\\]+", "");
    }

    /**
     * 为带 Archwing 展示标记的名称保留原名及可输入名称，不改变完整业务路径或其他限定词。
     *
     * @param value 官方或自维护记录的完整名称。
     * @return 可建立索引的名称入口。
     */
    public static List<String> itemSearchNames(String value) {
        if (CharSequenceUtil.isBlank(value))
            return List.of();
        String plain = ARCHWING_DISPLAY_MARKER.matcher(value).replaceFirst("");
        return plain.equals(value) ? List.of(value) : List.of(value, plain);
    }

    /**
     * 按周紫卡代码簿的名称规则规范化武器名，保留该接口要求的标点语义。
     *
     * @param value 待校验或规范化的文本。
     * @return 可用于周紫卡代码簿匹配的武器名称键。
     */
    public static String weeklyWeapon(String value) {
        return CharSequenceUtil.trim(Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFKC))
            .replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * 规范化完整展示短语，保留标点与单词边界，避免把不同词组拼成同一个键。
     *
     * @param value 原始展示短语。
     * @return 忽略大小写及连续空白的整词匹配键。
     */
    public static String phrase(String value) {
        return CharSequenceUtil.trim(Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFKC))
            .replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * 判断来自已命中官方记录的文本是否可向玩家展示，而不要求它一定有汉字。
     *
     * @param value 记录提供的名称或语言文本。
     * @return 不属于内部路径或大写下划线代码时为真。
     */
    public static boolean isReadableDisplayName(String value) {
        if (CharSequenceUtil.isBlank(value))
            return false;
        String name = CharSequenceUtil.trim(value);
        return !name.startsWith("/") && !name.matches("[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)+");
    }

    /**
     * 判断名称是否具有中文内容，避免将原始英文误标记为已本地化。
     *
     * @param value 待展示的名称或描述。
     * @return 包含中文字符时为真。
     */
    public static boolean hasChinese(String value) {
        return value != null
            && value.codePoints().anyMatch(code -> Character.UnicodeScript.of(code) == Character.UnicodeScript.HAN);
    }
}
