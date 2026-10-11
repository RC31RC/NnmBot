package com.nnm.bot.warframe.pojo;

import cn.hutool.core.collection.CollUtil;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Warframe Market 单条订单的结构化投影。
 */
public record WarframeMarketOrder(
        String id,
        String orderType,
        int platinum,
        int quantity,
        Integer perTrade,
        String subtype,
        Integer rank,
        Integer charges,
        Integer amberStars,
        Integer cyanStars,
        boolean visible,
        String createdAt,
        String updatedAt,
        String itemId,
        String groupId,
        WarframeMarketUser user,
        Map<String, Object> rawFields) {

    public WarframeMarketOrder {
        id = trim(id);
        orderType = trim(orderType);
        subtype = trim(subtype);
        createdAt = trim(createdAt);
        updatedAt = trim(updatedAt);
        itemId = trim(itemId);
        groupId = trim(groupId);
        user = user == null ? WarframeMarketUser.empty() : user;
        rawFields = CollUtil.isEmpty(rawFields) ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(rawFields));
    }

    /**
     * 兼容现有命令层使用的最小订单构造器。
     */
    public WarframeMarketOrder(
            String orderType,
            String userName,
            String reputation,
            int platinum,
            int quantity,
            Integer modRank,
            Integer amberStars,
            Integer cyanStars,
            String status) {
        this("", orderType, platinum, quantity, null, "", modRank, null, amberStars, cyanStars,
                true, "", "", "", "", new WarframeMarketUser(
                        "", userName, "", parseInteger(reputation), "", false, "", status,
                        "", "", Map.of(), Map.of()), Map.of());
    }

    /**
     * 兼容旧字段命名；新代码优先访问 {@link #user()} 和 {@link #rank()}。
     */
    public String userName() {
        return user.ingameName();
    }

    public String reputation() {
        return user.reputation() == null ? "" : String.valueOf(user.reputation());
    }

    public String status() {
        return user.status();
    }

    public Integer modRank() {
        return rank;
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static Integer parseInteger(String value) {
        try {
            return value == null || value.isBlank() ? null : Integer.valueOf(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
