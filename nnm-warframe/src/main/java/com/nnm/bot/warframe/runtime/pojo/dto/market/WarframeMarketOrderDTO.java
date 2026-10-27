package com.nnm.bot.warframe.runtime.pojo.dto.market;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Warframe Market 单条订单的结构化投影。
 */
public record WarframeMarketOrderDTO(
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
        WarframeMarketUserDTO user,
        Map<String, Object> rawFields) {

    /**
     * 构造市场订单传输对象，并保存初始化参数。
     *
     * @param id 目标记录的本地主键或协议标识。
     * @param orderType 订单类型。
     * @param platinum 铂金。
     * @param quantity 数量。
     * @param perTrade 每次交易。
     * @param subtype 子类型。
     * @param rank 等级。
     * @param charges 充能。
     * @param amberStars 琥珀星槽。
     * @param cyanStars 青蓝星槽。
     * @param visible 公开状态。
     * @param createdAt 创建时间时刻。
     * @param updatedAt 更新时间时刻。
     * @param itemId 当前物品的本地主键或上游标识。
     * @param groupId 订阅所属群组的本地主键。
     * @param user 用户。
     * @param rawFields 原始字段。
     */
    public WarframeMarketOrderDTO {
        id = trim(id);
        orderType = trim(orderType);
        subtype = trim(subtype);
        createdAt = trim(createdAt);
        updatedAt = trim(updatedAt);
        itemId = trim(itemId);
        groupId = trim(groupId);
        user = user == null ? WarframeMarketUserDTO.empty() : user;
        rawFields = CollUtil.isEmpty(rawFields) ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(rawFields));
    }

    /**
     * 兼容现有命令层使用的最小订单构造器。
     *
     * @param orderType 订单类型。
     * @param userName 用户名称。
     * @param reputation 信誉。
     * @param platinum 铂金。
     * @param quantity 数量。
     * @param modRank 振幅晶体等级。
     * @param amberStars 琥珀星槽。
     * @param cyanStars 青蓝星槽。
     * @param status 状态。
     */
    public WarframeMarketOrderDTO(
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
                true, "", "", "", "", new WarframeMarketUserDTO(
                        "", userName, "", parseInteger(reputation), "", false, "", status,
                        "", "", Map.of(), Map.of()), Map.of());
    }

    /**
     * 兼容旧字段命名；新代码优先访问 {@link #user()} 和 {@link #rank()}。
     *
     * @return 用户名称对应的文本。
     */
    public String userName() {
        return user.ingameName();
    }

    /**
     * 读取用户的交易信誉值。
     *
     * @return 用户的交易信誉值对应的文本。
     */
    public String reputation() {
        return user.reputation() == null ? "" : String.valueOf(user.reputation());
    }

    /**
     * 读取当前状态的业务表示。
     *
     * @return 当前状态的业务表示对应的文本。
     */
    public String status() {
        return user.status();
    }

    /**
     * 读取订单对应的振幅晶体等级。
     * @return 当前操作计算或读取的数值。
     */
    public Integer modRank() {
        return rank;
    }

    /**
     * 裁剪首尾空白并统一缺省文本。
     *
     * @param value 待校验或规范化的文本。
     * @return 去除首尾空白的文本；输入缺省时返回空文本。
     */
    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * 将明确的数字文本转换为整数，格式错误直接失败。
     *
     * @param value 待校验或规范化的文本。
     * @return 解析后的整数；输入为空白时返回空值，格式错误直接抛出。
     */
    private static Integer parseInteger(String value) {
        return StrUtil.isBlank(value) ? null : Integer.valueOf(value.trim());
    }
}
