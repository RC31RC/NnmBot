package com.nnm.bot.warframe.runtime.mapper.worldstate;

import cn.hutool.core.collection.CollUtil;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 以同条结构化奖励中已解析的名称替换文字摘要的物品路径，不重复添加数量。 */
public final class WarframeRewardSummaryFormatter {
    private static final Pattern INTERNAL_REFERENCE = Pattern.compile("/(?:Lotus|EE)/[^,，\\s]+");

    private WarframeRewardSummaryFormatter() {}

    /**
     * 按完整原始路径替换已确认的奖励名称，不触及未命中的路径或相似前缀。
     *
     * @param summary 官方奖励摘要。
     * @param replacements 原始路径与已确认名称的映射。
     * @return 仅替换确认命中的摘要。
     */
    public static String replaceKnownReferences(String summary, Map<String, String> replacements) {
        if (replacements.isEmpty())
            return summary;
        Matcher matcher = INTERNAL_REFERENCE.matcher(summary);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String name = replacements.getOrDefault(matcher.group(), matcher.group());
            matcher.appendReplacement(result, Matcher.quoteReplacement(name));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 仅当内部路径数与已解析物品数完全相同时，按原顺序替换摘要中的物品路径。
     *
     * @param summary 带路径与数量的官方摘要。
     * @param names 已解析的同序物品名称。
     * @return 替换路径并保留数量的摘要；数量不匹配时返回原文。
     */
    public static String replaceInternalReferences(String summary, List<String> names) {
        // 数量不一致时不能按顺序猜测未知引用对应哪个已解析物品。
        if (CollUtil.isEmpty(names) || INTERNAL_REFERENCE.matcher(summary).results().count() != names.size())
            return summary;
        Matcher matcher = INTERNAL_REFERENCE.matcher(summary);
        StringBuffer result = new StringBuffer();
        int index = 0;
        while (matcher.find()) {
            if (index >= names.size())
                break;
            matcher.appendReplacement(result, Matcher.quoteReplacement(names.get(index++)));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
