package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.util.StrUtil;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** 原始字段和展示投影共用的键与文本操作，仅服务世界状态投影。 */
final class WarframeWorldStateProjectionFields {
    private WarframeWorldStateProjectionFields() {}

    /**
     * 将已经序列化的展示映射统一为文本键。
     *
     * @param source 已解析的业务映射。
     * @return 保留原值的文本键映射。
     */
    static Map<String, Object> toStringKeys(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, value) -> result.put(String.valueOf(key), value));
        return result;
    }

    /**
     * 仅给缺少说明的展示记录补齐已确认的静态信息。
     *
     * @param display 当前展示行。
     * @param key 要补充的展示字段。
     * @param value 已核实的文本值。
     */
    static void putIfAbsent(Map<String, Object> display, String key, String value) {
        if (StrUtil.isNotBlank(value) && (display.get(key) == null || "".equals(display.get(key)))) {
            display.put(key, value);
        }
    }

    /**
     * 对齐 Repository 返回的大小写不敏感业务键。
     *
     * @param value 官方业务键。
     * @return 查询投影使用的规范键。
     */
    static String normalize(String value) {
        return StrUtil.emptyIfNull(value).toLowerCase(Locale.ROOT);
    }

    /**
     * 从原始字段中读取非空文本。
     *
     * @param value 待提取的字段值。
     * @return 非空文本或空。
     */
    static String text(Object value) {
        return value instanceof String string && StrUtil.isNotBlank(string) ? string : null;
    }

    /**
     * 按本地化优先级取第一个有效文本。
     *
     * @param values 按优先级排列的候选。
     * @return 首个有效文本或空。
     */
    static String first(String... values) {
        for (String value : values)
            if (StrUtil.isNotBlank(value))
                return value;
        return null;
    }

}
