package com.nnm.bot.warframe.pojo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Warframe Market 订单中返回的用户概要。
 *
 * <p>用户状态和最后在线时间属于订单查询时的动态信息，不能从本地物品
 * 数据推断，因此与物品数据分开建模。</p>
 */
public record WarframeMarketUser(
        String id,
        String ingameName,
        String slug,
        Integer reputation,
        String platform,
        boolean crossplay,
        String locale,
        String status,
        String avatar,
        String lastSeen,
        Map<String, Object> activity,
        Map<String, Object> rawFields) {

    public WarframeMarketUser {
        id = trim(id);
        ingameName = trim(ingameName);
        slug = trim(slug);
        platform = trim(platform);
        locale = trim(locale);
        status = trim(status);
        avatar = trim(avatar);
        lastSeen = trim(lastSeen);
        activity = immutable(activity);
        rawFields = immutable(rawFields);
    }

    public static WarframeMarketUser empty() {
        return new WarframeMarketUser("", "", "", null, "", false, "", "", "", "", Map.of(), Map.of());
    }

    public boolean present() {
        return StrUtil.isNotBlank(id) || StrUtil.isNotBlank(ingameName);
    }

    private static String trim(String value) {
        return StrUtil.emptyIfNull(value).trim();
    }

    private static Map<String, Object> immutable(Map<String, Object> values) {
        return CollUtil.isEmpty(values)
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }
}
