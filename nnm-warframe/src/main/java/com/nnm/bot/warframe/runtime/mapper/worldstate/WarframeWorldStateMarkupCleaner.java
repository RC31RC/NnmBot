package com.nnm.bot.warframe.runtime.mapper.worldstate;

import cn.hutool.core.text.CharSequenceUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** 仅清理玩家展示层的游戏文本标记；原始上游快照保持原样供审核。 */
public final class WarframeWorldStateMarkupCleaner {
    private static final Pattern DISPLAY_MARKERS = Pattern.compile("<DT_[A-Z0-9_]+>|\\|[A-Z][A-Z0-9_]*\\|");

    private WarframeWorldStateMarkupCleaner() {}

    /** @param text 可能包含游戏颜色或阵营标记的描述；@return 可直接展示的文本。 */
    public static String clean(String text) {
        return text == null ? null : CharSequenceUtil.trim(DISPLAY_MARKERS.matcher(text).replaceAll(""));
    }

    /** @param value 展示字段或嵌套集合；@return 只清理标记的新展示值。 */
    public static Object cleanValue(Object value) {
        if (value instanceof String text)
            return clean(text);
        if (value instanceof List<?> list)
            return list.stream().map(WarframeWorldStateMarkupCleaner::cleanValue).toList();
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> clean = new LinkedHashMap<>();
            map.forEach((key, entry) -> clean.put(String.valueOf(key), cleanValue(entry)));
            return clean;
        }
        return value;
    }
}
