package com.nnm.bot.warframe.plugin.persistent.curated.watch;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** NnmBot 自身的订阅参数语法，不导入其他机器人的命令别名。 */
public record WarframeSubscriptionSpec(String type, String key, String value) {
    private static final Map<String, String> TOPICS = Map.ofEntries(
            Map.entry("平原", "cetus-night"), Map.entry("奸商", "void-trader"),
            Map.entry("特惠", "daily-deals"), Map.entry("钢精", "steel-path"),
            Map.entry("警报", "alerts"), Map.entry("活动", "events"),
            Map.entry("入侵", "invasions"), Map.entry("圣所", "cavia"),
            Map.entry("扎里曼", "holdfasts"), Map.entry("六人组", "hex-bounties"),
            Map.entry("六人组箱", "hex-caches"), Map.entry("仲裁", "arbitration"),
            Map.entry("裂缝", "fissures"));

    /**
     * 将外部输入解析为明确的业务数据对象。
     *
     * @param arguments 当前命令或操作的参数。
     * @return 游戏资料订阅规格对象。
     */
    public static WarframeSubscriptionSpec parse(String arguments) {
        String input = arguments == null ? "" : arguments.trim();
        if (StrUtil.isBlank(input) || input.length() > 500) return null;
        String[] parts = input.split("\\s+", 2);
        String type = TOPICS.get(parts[0]);
        if (type == null) return null;
        String filters = parts.length == 1 ? "*" : parts[1];
        List<String> values = Arrays.stream(filters.split("[\\s|｜]+"))
                .map(String::trim).filter(value -> StrUtil.isNotEmpty(value))
                .map(value -> value.toLowerCase(Locale.ROOT)).distinct().sorted().toList();
        if (CollUtil.isEmpty(values) || values.size() > 20
                || values.stream().anyMatch(value -> value.length() > 100)) return null;
        if (!List.of("arbitration", "fissures").contains(type)
                && (values.size() != 1 || !"*".equals(values.getFirst()))) return null;
        if (values.size() > 1 && values.contains("*")) return null;
        return new WarframeSubscriptionSpec(type, "filter", String.join("|", values));
    }
}
